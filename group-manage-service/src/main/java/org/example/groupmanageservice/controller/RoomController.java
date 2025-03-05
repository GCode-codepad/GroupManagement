package org.example.groupmanageservice.controller;

import org.example.groupmanageservice.domain.Room;
import org.example.groupmanageservice.service.RoomService;
// import org.example.groupmanageservice.service.RoomService.PermissionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1")
public class RoomController {

    private final RoomService roomService = new RoomService();

    @GetMapping("/")
    public String getMethodName() {
        return "OK";
    }
    

    // @PostMapping("/rooms")
    // public ResponseEntity<?> createRoom(@RequestBody Map<String, String> request) {
    //     String hosterUserId = request.get("hosterUserId");
    //     Room room = roomService.createRoom(hosterUserId);
    //     return new ResponseEntity<>(Map.of("roomId", room.getRoomId(), "joinPassword", room.getJoinPassword()), HttpStatus.CREATED);
    // }

    // @GetMapping("/rooms/{roomId}")
    // public ResponseEntity<?> getRoom(@PathVariable String roomId) {
    //     Room room = roomService.getRoom(roomId);
    //     return ResponseEntity.ok(Map.of(
    //             "roomId", room.getRoomId(),
    //             "hosterUserId", room.getHosterUserId(),
    //             "status", room.getStatus()
    //     ));
    // }

    // @PostMapping("/rooms/{roomId}/close")
    // public ResponseEntity<?> closeRoom(@PathVariable String roomId, @RequestBody Map<String, String> request) {
    //     String requestingUserId = request.get("requestingUserId");
    //     roomService.closeRoom(roomId, requestingUserId);
    //     return ResponseEntity.ok().build();
    // }

    // @GetMapping("/rooms")
    // public ResponseEntity<List<Room>> listRooms() {
    //     List<Room> rooms = roomService.listRooms();
    //     return ResponseEntity.ok(rooms);
    // }

    // @PostMapping("/rooms/{roomId}/join")
    // public ResponseEntity<?> joinRoom(@PathVariable String roomId, @RequestBody Map<String, String> request) {
    //     String userId = request.get("userId");
    //     String joinPassword = request.get("joinPassword");
    //     roomService.joinRoom(roomId, userId, joinPassword);
    //     return ResponseEntity.ok().build();
    // }

    // @PostMapping("/rooms/{roomId}/exit")
    // public ResponseEntity<?> exitRoom(@PathVariable String roomId, @RequestBody Map<String, String> request) {
    //     String userId = request.get("userId");
    //     roomService.exitRoom(roomId, userId);
    //     return ResponseEntity.ok().build();
    // }

    // @PostMapping("/rooms/{roomId}/participants/kick")
    // public ResponseEntity<?> kickUser(@PathVariable String roomId, @RequestBody Map<String, String> request) {
    //     String requestingUserId = request.get("requestingUserId");
    //     String kickedUserId = request.get("kickedUserId");
    //     roomService.kickUser(roomId, requestingUserId, kickedUserId);
    //     return ResponseEntity.ok().build();
    // }

    // @GetMapping("/permissions/{roomId}/{userId}")
    // public ResponseEntity<?> getPermissions(@PathVariable String roomId, @PathVariable String userId) {
    //     PermissionResponse response = (PermissionResponse) roomService.getPermissions(roomId, userId);
    //     return ResponseEntity.ok(response);
    // }
}
