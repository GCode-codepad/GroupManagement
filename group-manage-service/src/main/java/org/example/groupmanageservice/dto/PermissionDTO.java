package org.example.groupmanageservice.dto;

import java.util.List;

import org.example.groupmanageservice.enums.Roles;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDTO {
    private String userId;
    private String roomId;
    private String permissions;
    private List<Roles> roles;
}
