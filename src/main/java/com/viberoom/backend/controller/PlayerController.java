package com.viberoom.backend.controller;

import com.viberoom.backend.model.PlayerState;
import com.viberoom.backend.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms/{roomId}/player")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<PlayerState> getState(@PathVariable String roomId) {
        return ResponseEntity.ok(playerService.getState(roomId));
    }

    @PostMapping("/play-pause")
    public ResponseEntity<?> playPause(@PathVariable String roomId, @RequestBody Map<String, Object> body) {
        boolean isPlaying = (boolean) body.get("isPlaying");
        playerService.playPause(roomId, isPlaying);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/seek")
    public ResponseEntity<?> seek(@PathVariable String roomId, @RequestBody Map<String, Object> body) {
        double timestamp = ((Number) body.get("timestamp")).doubleValue();
        playerService.seek(roomId, timestamp);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/skip")
    public ResponseEntity<?> skip(@PathVariable String roomId, @RequestBody Map<String, Object> body) {
        String direction = (String) body.get("direction");
        playerService.skip(roomId, direction);
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
