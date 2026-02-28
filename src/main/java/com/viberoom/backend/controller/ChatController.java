package com.viberoom.backend.controller;

import com.viberoom.backend.model.ChatMessage;
import com.viberoom.backend.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms/{roomId}/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getHistory(@PathVariable String roomId,
                                                        @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(chatService.getHistory(roomId, limit));
    }

    @PostMapping
    public ResponseEntity<ChatMessage> sendMessage(@PathVariable String roomId,
                                                   @RequestBody Map<String, String> body, Authentication auth) {
        String uid = auth != null ? auth.getName() : "guest";
        return ResponseEntity.ok(chatService.sendMessage(roomId, uid, body.get("text")));
    }
}
