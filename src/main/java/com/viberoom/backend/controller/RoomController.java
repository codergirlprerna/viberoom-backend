package com.viberoom.backend.controller;

import com.google.firebase.auth.FirebaseToken;
import com.viberoom.backend.dto.CreateRoomRequest;
import com.viberoom.backend.dto.JoinGuestRequest;
import com.viberoom.backend.model.Room;
import com.viberoom.backend.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/public")
    public ResponseEntity<List<Room>> getPublicRooms() {
        return ResponseEntity.ok(roomService.getPublicRooms());
    }

    @GetMapping("/my")
    public ResponseEntity<List<Room>> getMyRooms(Authentication auth) {
        return ResponseEntity.ok(roomService.getRoomsByHost(auth.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoom(@PathVariable String id) {
        return ResponseEntity.ok(roomService.getRoom(id));
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@Valid @RequestBody CreateRoomRequest req, Authentication auth) {
        FirebaseToken token = (FirebaseToken) auth.getDetails();
        String displayName  = token != null ? token.getName() : "Host";
        return ResponseEntity.ok(roomService.createRoom(req, auth.getName(), displayName));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<?> join(@PathVariable String id, Authentication auth) {
        roomService.join(id, auth.getName());
        return ResponseEntity.ok(Map.of("joined", true));
    }

    @PostMapping("/{id}/join/guest")
    public ResponseEntity<?> joinGuest(@PathVariable String id, @Valid @RequestBody JoinGuestRequest req) {
        String guestToken = roomService.joinAsGuest(id, req.getGuestName());
        return ResponseEntity.ok(Map.of("guestToken", guestToken, "joined", true));
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<?> leave(@PathVariable String id, Authentication auth) {
        roomService.leave(id, auth != null ? auth.getName() : "guest");
        return ResponseEntity.ok(Map.of("left", true));
    }

    @GetMapping("/{id}/listeners")
    public ResponseEntity<?> getListeners(@PathVariable String id) {
        return ResponseEntity.ok(roomService.getListeners(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable String id, Authentication auth) {
        roomService.deleteRoom(id, auth.getName());
        return ResponseEntity.ok(Map.of("deleted", true));
    }
}
