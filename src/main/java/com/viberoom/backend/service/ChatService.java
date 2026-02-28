package com.viberoom.backend.service;

import com.viberoom.backend.model.ChatMessage;
import com.viberoom.backend.repository.ChatMessageRepository;
import com.viberoom.backend.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository        userRepository;
    private final SimpMessagingTemplate messaging;

    public ChatService(ChatMessageRepository chatMessageRepository,
                       UserRepository userRepository,
                       SimpMessagingTemplate messaging) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository        = userRepository;
        this.messaging             = messaging;
    }

    public List<ChatMessage> getHistory(String roomId, int limit) {
        List<ChatMessage> msgs = chatMessageRepository
                .findByRoomIdOrderByCreatedAtDesc(roomId, PageRequest.of(0, limit));
        Collections.reverse(msgs);
        return msgs;
    }

    public ChatMessage sendMessage(String roomId, String senderId, String text) {
        String senderName  = userRepository.findById(senderId)
                .map(u -> u.getDisplayName() != null ? u.getDisplayName() : u.getEmail())
                .orElse("Guest");
        String avatarColor = userRepository.findById(senderId)
                .map(u -> u.getAvatarColor()).orElse("#c8d8c8");

        ChatMessage msg = new ChatMessage();
        msg.setRoomId(roomId);
        msg.setSenderId(senderId);
        msg.setSenderName(senderName);
        msg.setAvatarColor(avatarColor);
        msg.setText(text);
        msg.setCreatedAt(LocalDateTime.now());

        msg = chatMessageRepository.save(msg);
        messaging.convertAndSend("/topic/room/" + roomId + "/chat", msg);
        return msg;
    }
}
