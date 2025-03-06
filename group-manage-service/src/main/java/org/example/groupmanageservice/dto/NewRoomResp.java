package org.example.groupmanageservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewRoomResp {
    private String roomId;
    private String joinPassword;
}