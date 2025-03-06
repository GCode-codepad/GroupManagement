package org.example.groupmanageservice.service;

import org.example.groupmanageservice.client.UserServiceClient;
import org.example.groupmanageservice.client.UserServiceClient.User;
import org.example.groupmanageservice.exception.ForbiddenException;
import org.example.groupmanageservice.exception.ResourceNotFoundException;
import org.example.groupmanageservice.util.dtoConverter;

import com.gs.fw.common.mithra.MithraManagerProvider;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.example.groupmanageservice.domain.Room;
import org.example.groupmanageservice.domain.RoomFinder;
import org.example.groupmanageservice.domain.RoomList;
import org.example.groupmanageservice.dto.PermissionDTO;
import org.example.groupmanageservice.dto.RoomDTO;
import org.example.groupmanageservice.enums.Roles;
import org.example.groupmanageservice.domain.Participant;
import org.example.groupmanageservice.domain.ParticipantFinder;

public class RoomService {

    private final UserServiceClient userServiceClient = new UserServiceClient();

    public Room createRoom(String hosterUserId) {
        // 调用用户服务获取用户信息
        User hoster = userServiceClient.getUserById(hosterUserId);
        if(hoster == null) {
            throw new ResourceNotFoundException("Hoster user not found");
        }
        if(!(hoster.getRoles().contains(Roles.HOSTER) || hoster.getRoles().contains(Roles.SYSTEM_ADMIN))) {
            throw new ForbiddenException("User not authorized to create room");
        }
        // 生成 roomId 与 joinPassword
        String roomId = UUID.randomUUID().toString();
        String joinPassword = UUID.randomUUID().toString().substring(0, 8);
        MithraManagerProvider.getMithraManager().executeTransactionalCommand((tx) -> {
            Room room = new Room();
            room.setRoomId(roomId);
            room.setHosterUserId(hosterUserId);
            room.setJoinPassword(joinPassword);
            room.setStatus("open");
            
            // 创建房间时，hoster自动加入房间
            Participant participant = new Participant();
            participant.setUserId(hosterUserId);
            participant.setPermissions("");

            room.getParticipants().add(participant);

            room.cascadeInsert();
            return null;
        });
        return RoomFinder.findOne(RoomFinder.roomId().eq(roomId));
    }

    public Room getRoom(String roomId) {
        Room room = RoomFinder.findOne(RoomFinder.roomId().eq(roomId));
        if(room == null) {
            throw new ResourceNotFoundException("Room not found");
        }
        return room;
    }

    public void closeRoom(String roomId, String requestingUserId) {
        MithraManagerProvider.getMithraManager().executeTransactionalCommand((tx) -> {
            Room room = getRoom(roomId);
            User requester = userServiceClient.getUserById(requestingUserId);
            if(requester == null) {
                throw new ResourceNotFoundException("Requesting user not found");
            }
            // 判断请求者是否有权限关闭房间：必须为房间原hoster或system_admin
            if(!(requester.getRoles().contains(Roles.SYSTEM_ADMIN) || room.getHosterUserId().equals(requestingUserId))) {
                throw new ForbiddenException("User not authorized to close room");
            }
            // 更新房间状态
            room.setStatus("closed");

            // 删除房间内所有参与者及权限（此处简化处理）
            room.getParticipants().deleteAll();
            return null;
        });
        
    }

    public List<RoomDTO> listRooms() {
        // 此处先不做额外权限校验，直接返回所有房间列表
        RoomList allRoom =  RoomFinder.findMany(RoomFinder.all()).getDetachedCopy();
        List<RoomDTO> allDtos = allRoom.stream()
                .map(r -> dtoConverter.convert2DTO(r))
                .collect(Collectors.toList());
        return allDtos;
    }

    public void joinRoom(String roomId, String userId, String joinPassword) {
        Room room = getRoom(roomId).getDetachedCopy();
        if(!room.getJoinPassword().equals(joinPassword)) {
            throw new IllegalArgumentException("Incorrect join password");
        }
        User user = userServiceClient.getUserById(userId);
        if(user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        MithraManagerProvider.getMithraManager().executeTransactionalCommand((tx) -> {
            // 检查用户是否已在参与者列表中
            Participant existing = ParticipantFinder.findOne(
                    ParticipantFinder.roomId().eq(roomId)
                    .and(ParticipantFinder.userId().eq(userId))
            );
            if(existing != null) {
                throw new IllegalArgumentException("User already in room");
            }
            Participant participant = new Participant();
            // participant.setRoom(room);
            participant.setUserId(userId);
            participant.setPermissions("");

            Room room2update = getRoom(roomId);
            room2update.getParticipants().add(participant);
            // room2update.cascadeInsert();

            return null;
        });
    }

    public void exitRoom(String roomId, String userId) {
        MithraManagerProvider.getMithraManager().executeTransactionalCommand((tx) -> {
            Room room = getRoom(roomId);
            User user = userServiceClient.getUserById(userId);
            if(user == null) {
                throw new ResourceNotFoundException("User not found");
            }
            Participant participant = ParticipantFinder.findOne(
                    ParticipantFinder.roomId().eq(roomId)
                    .and(ParticipantFinder.userId().eq(userId))
            );
            if(participant == null) {
                throw new ResourceNotFoundException("User not in the room");
            }
            participant.delete();
            // 如果退出者为原hoster，则进行hoster退出逻辑
            if(room.getHosterUserId().equals(userId)) {
                List<Participant> remaining = ParticipantFinder.findMany(ParticipantFinder.roomId().eq(roomId));
                if(!remaining.isEmpty()){
                    // 选取第一个用户作为新的hoster，此处简单按照userId排序
                    remaining.sort((a, b) -> a.getUserId().compareTo(b.getUserId()));
                    room.setHosterUserId(remaining.get(0).getUserId());
                    // 更新可能的其他用户权限

                } else {
                    // 无其他参与者，则自动关闭房间
                    // 更新房间状态
                    room.setStatus("closed");

                    // 删除房间内所有参与者及权限（此处简化处理）
                    room.getParticipants().deleteAll();
                }
            }
            return null;
        });
    }

    public void kickUser(String roomId, String requestingUserId, String kickedUserId) {
        MithraManagerProvider.getMithraManager().executeTransactionalCommand((tx) -> {
            Room room = getRoom(roomId);
            User requester = userServiceClient.getUserById(requestingUserId);
            if(requester == null) {
                throw new ResourceNotFoundException("Requesting user not found");
            }
            if(requestingUserId.equals(kickedUserId)) {
                throw new IllegalArgumentException("Cannot kick self");
            }
            // 检查权限：需为room的hoster或system_admin
            if(!(requester.getRoles().contains(Roles.SYSTEM_ADMIN) || room.getHosterUserId().equals(requestingUserId))) {
                throw new ForbiddenException("User not authorized to kick participant");
            }
            User kickedUser = userServiceClient.getUserById(kickedUserId);
            if(kickedUser == null) {
                throw new ResourceNotFoundException("Kicked user not found");
            }
            Participant participant = ParticipantFinder.findOne(
                    ParticipantFinder.roomId().eq(roomId)
                    .and(ParticipantFinder.userId().eq(kickedUserId))
            );
            if(participant == null) {
                throw new ResourceNotFoundException("Kicked user not in room");
            }
            participant.delete();
            return null;
        });
    }

    public PermissionDTO getPermissions(String roomId, String userId) {
        PermissionDTO permissions = new PermissionDTO();

        // 此处简化返回用户在该房间的权限信息
        Room room = getRoom(roomId);
        User user = userServiceClient.getUserById(userId);
        if(user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        // 检查用户是否在房间内
        Participant participant = ParticipantFinder.findOne(
                ParticipantFinder.roomId().eq(roomId)
                .and(ParticipantFinder.userId().eq(userId))
        );
        if(participant == null) {
            throw new ResourceNotFoundException("Kicked user not in room");
        }

        // 如果用户是房间hoster则返回hoster权限，否则删除hoster权限
        permissions.setRoomId(roomId);
        permissions.setUserId(userId);
        permissions.setPermissions(participant.getPermissions());
        
        Set<Roles> userRoles = new HashSet<Roles>(user.getRoles());
        if(room.getHosterUserId().equals(userId)) {
            userRoles.add(Roles.HOSTER);
        }else{
            userRoles.remove(Roles.HOSTER);
            userRoles.add(Roles.REGULAR_USER);
        }
        permissions.setRoles(List.copyOf(userRoles));

        return permissions;
    }
    
}
