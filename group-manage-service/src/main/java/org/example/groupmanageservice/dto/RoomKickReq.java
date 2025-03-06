package org.example.groupmanageservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomKickReq {
    private String requestingUserId;
    private String kickedUserId;
}
