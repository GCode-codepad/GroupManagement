package org.example.groupmanageservice.util;

import org.example.groupmanageservice.domain.Participant;
import org.example.groupmanageservice.domain.Room;
import org.example.groupmanageservice.dto.ParticipantDTO;
import org.example.groupmanageservice.dto.RoomDTO;

public class dtoConverter {

    public static RoomDTO convert2DTO(Room room) {
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setRoomId(room.getRoomId());
        roomDTO.setHosterUserId(room.getHosterUserId());
        roomDTO.setStatus(room.getStatus());
        return roomDTO;
    }
    
    public static ParticipantDTO convert2DTO(Participant participant) {
        ParticipantDTO participantDTO = new ParticipantDTO();
        participantDTO.setUserId(participant.getUserId());
        participantDTO.setPermissions(participant.getPermissions());
        return participantDTO;
    }
}
