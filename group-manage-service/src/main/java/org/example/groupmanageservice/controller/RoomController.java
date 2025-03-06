package org.example.groupmanageservice.controller;

import org.example.groupmanageservice.domain.Room;
import org.example.groupmanageservice.dto.CloseRoomReq;
import org.example.groupmanageservice.dto.ExitRoomReq;
import org.example.groupmanageservice.dto.JoinRoomReq;
import org.example.groupmanageservice.dto.NewRoomReq;
import org.example.groupmanageservice.dto.NewRoomResp;
import org.example.groupmanageservice.dto.PermissionDTO;
import org.example.groupmanageservice.dto.RoomDTO;
import org.example.groupmanageservice.dto.RoomKickReq;
import org.example.groupmanageservice.service.RoomService;
import org.example.groupmanageservice.util.dtoConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/v1")
public class RoomController {

    private final RoomService roomService = new RoomService();

    @GetMapping("/")
    public String getMethodName() {
        return "OK";
    }
    

    @PostMapping("/rooms")
    public ResponseEntity<NewRoomResp> createRoom(@RequestBody NewRoomReq request) {
        String hosterUserId = request.getHosterUserId();
        Room room = roomService.createRoom(hosterUserId);
        NewRoomResp newRoomResp = new NewRoomResp(room.getRoomId(), room.getJoinPassword());
        return new ResponseEntity<>(newRoomResp, HttpStatus.CREATED);
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<RoomDTO> getRoom(@PathVariable String roomId) {
        Room room = roomService.getRoom(roomId.trim()).getDetachedCopy();
        RoomDTO roomdto = dtoConverter.convert2DTO(room);
        return ResponseEntity.ok(roomdto);
    }

    @PostMapping("/rooms/{roomId}/close")
    public ResponseEntity<String> closeRoom(@PathVariable String roomId, @RequestBody CloseRoomReq request) {
        String requestingUserId = request.getRequestingUserId();
        roomService.closeRoom(roomId, requestingUserId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<RoomDTO>> listRooms() {
        List<RoomDTO> rooms = roomService.listRooms();
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<String> joinRoom(@PathVariable String roomId, @RequestBody JoinRoomReq request) {
        String userId = request.getUserId();
        String joinPassword = request.getJoinPassword();
        roomService.joinRoom(roomId, userId, joinPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rooms/{roomId}/exit")
    public ResponseEntity<String> exitRoom(@PathVariable String roomId, @RequestBody ExitRoomReq request) {
        String userId = request.getUserId();
        roomService.exitRoom(roomId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rooms/{roomId}/participants/kick")
    public ResponseEntity<String> kickUser(@PathVariable String roomId, @RequestBody RoomKickReq request) {
        String requestingUserId = request.getRequestingUserId();
        String kickedUserId = request.getKickedUserId();
        roomService.kickUser(roomId, requestingUserId, kickedUserId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/permissions/{roomId}/{userId}")
    public ResponseEntity<PermissionDTO> getPermissions(@PathVariable String roomId, @PathVariable String userId) {
        PermissionDTO response = roomService.getPermissions(roomId, userId);
        return ResponseEntity.ok(response);
    }
}
