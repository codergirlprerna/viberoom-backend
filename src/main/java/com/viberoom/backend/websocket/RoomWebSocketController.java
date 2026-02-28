package com.viberoom.backend.websocket;

import com.viberoom.backend.service.ChatService;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.Map;

@Controller
public class RoomWebSocketController {

    private final SimpMessagingTemplate messaging;
    private final ChatService           chatService;

    public RoomWebSocketController(SimpMessagingTemplate messaging, ChatService chatService) {
        this.messaging   = messaging;
        this.chatService = chatService;
    }

    @MessageMapping("/room/{roomId}/join")
    public void onJoin(@DestinationVariable String roomId, @Header("simpSessionId") String sessionId) {
        messaging.convertAndSend("/topic/room/" + roomId + "/presence",
                Map.of("event", "JOIN", "sessionId", sessionId));
    }

    @MessageMapping("/room/{roomId}/leave")
    public void onLeave(@DestinationVariable String roomId, @Header("simpSessionId") String sessionId) {
        messaging.convertAndSend("/topic/room/" + roomId + "/presence",
                Map.of("event", "LEAVE", "sessionId", sessionId));
    }

    @MessageMapping("/room/{roomId}/chat")
    public void onChat(@DestinationVariable String roomId,
                       @Payload Map<String, String> payload,
                       @Header("simpSessionId") String sessionId) {
        String text = payload.get("text");
        if (text != null && !text.isBlank()) {
            chatService.sendMessage(roomId, sessionId, text);
        }
    }

    @MessageMapping("/room/{roomId}/reaction")
    public void onReaction(@DestinationVariable String roomId, @Payload Map<String, String> payload) {
        messaging.convertAndSend("/topic/room/" + roomId + "/reactions",
                Map.of("emoji", payload.getOrDefault("emoji", "🔥")));
    }
}
