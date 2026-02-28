package com.viberoom.backend.service;

import com.viberoom.backend.model.PlayerState;
import com.viberoom.backend.model.QueueItem;
import com.viberoom.backend.repository.PlayerStateRepository;
import com.viberoom.backend.repository.QueueItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PlayerService {

    private final PlayerStateRepository playerStateRepository;
    private final QueueItemRepository   queueItemRepository;
    private final SimpMessagingTemplate messaging;

    public PlayerService(PlayerStateRepository playerStateRepository,
                         QueueItemRepository queueItemRepository,
                         SimpMessagingTemplate messaging) {
        this.playerStateRepository = playerStateRepository;
        this.queueItemRepository   = queueItemRepository;
        this.messaging             = messaging;
    }

    public PlayerState getState(String roomId) {
        return playerStateRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player state not found"));
    }

    public void playPause(String roomId, boolean isPlaying) {
        PlayerState state = getState(roomId);
        state.setPlaying(isPlaying);
        state.setUpdatedAt(LocalDateTime.now());
        playerStateRepository.save(state);
        messaging.convertAndSend("/topic/room/" + roomId + "/player",
                Map.of("type", "PLAY_PAUSE", "isPlaying", isPlaying, "timestamp", state.getTimestamp()));
    }

    public void seek(String roomId, double timestamp) {
        PlayerState state = getState(roomId);
        state.setTimestamp(timestamp);
        state.setUpdatedAt(LocalDateTime.now());
        playerStateRepository.save(state);
        messaging.convertAndSend("/topic/room/" + roomId + "/player",
                Map.of("type", "SEEK", "timestamp", timestamp));
    }

    public void skip(String roomId, String direction) {
        List<QueueItem> queue = queueItemRepository.findByRoomIdOrderByPositionAsc(roomId);
        if (queue.isEmpty()) return;
        int currentIdx = -1;
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).isCurrent()) { currentIdx = i; break; }
        }
        int nextIdx = direction.equals("next")
                ? Math.min(currentIdx + 1, queue.size() - 1)
                : Math.max(currentIdx - 1, 0);
        for (int i = 0; i < queue.size(); i++) queue.get(i).setCurrent(i == nextIdx);
        queueItemRepository.saveAll(queue);
        QueueItem nextSong = queue.get(nextIdx);
        PlayerState state = getState(roomId);
        state.setYoutubeId(nextSong.getYoutubeId());
        state.setTitle(nextSong.getTitle());
        state.setArtist(nextSong.getArtist());
        state.setTimestamp(0);
        state.setPlaying(true);
        state.setUpdatedAt(LocalDateTime.now());
        playerStateRepository.save(state);
        messaging.convertAndSend("/topic/room/" + roomId + "/player",
                Map.of("type", "SKIP", "song", nextSong));
    }
}
