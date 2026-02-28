package com.viberoom.backend.controller;

import com.viberoom.backend.model.QueueItem;
import com.viberoom.backend.repository.QueueItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms/{roomId}/queue")
public class QueueController {

    private final QueueItemRepository   queueItemRepository;
    private final SimpMessagingTemplate messaging;

    public QueueController(QueueItemRepository queueItemRepository, SimpMessagingTemplate messaging) {
        this.queueItemRepository = queueItemRepository;
        this.messaging           = messaging;
    }

    @GetMapping
    public ResponseEntity<List<QueueItem>> getQueue(@PathVariable String roomId) {
        return ResponseEntity.ok(queueItemRepository.findByRoomIdOrderByPositionAsc(roomId));
    }

    @PostMapping
    public ResponseEntity<QueueItem> addToQueue(@PathVariable String roomId, @RequestBody QueueItem item) {
        List<QueueItem> existing = queueItemRepository.findByRoomIdOrderByPositionAsc(roomId);
        item.setRoomId(roomId);
        item.setPosition(existing.size());
        item.setCurrent(existing.isEmpty());
        QueueItem saved  = queueItemRepository.save(item);
        List<QueueItem> updated = queueItemRepository.findByRoomIdOrderByPositionAsc(roomId);
        messaging.convertAndSend("/topic/room/" + roomId + "/player", Map.of("type", "QUEUE_UPDATE", "queue", updated));
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<?> removeFromQueue(@PathVariable String roomId, @PathVariable String songId) {
        queueItemRepository.deleteById(songId);
        List<QueueItem> updated = queueItemRepository.findByRoomIdOrderByPositionAsc(roomId);
        messaging.convertAndSend("/topic/room/" + roomId + "/player", Map.of("type", "QUEUE_UPDATE", "queue", updated));
        return ResponseEntity.ok(Map.of("deleted", true));
    }
}
