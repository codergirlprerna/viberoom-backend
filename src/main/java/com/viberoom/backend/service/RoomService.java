package com.viberoom.backend.service;

import com.viberoom.backend.dto.CreateRoomRequest;
import com.viberoom.backend.model.*;
import com.viberoom.backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {

    private final RoomRepository        roomRepository;
    private final PlayerStateRepository playerStateRepository;
    private final Map<String, Map<String, String>> roomListeners = new ConcurrentHashMap<>();

    public RoomService(RoomRepository roomRepository, PlayerStateRepository playerStateRepository) {
        this.roomRepository        = roomRepository;
        this.playerStateRepository = playerStateRepository;
    }

    public List<Room> getPublicRooms() {
        return roomRepository.findByIsPublicTrueAndActiveTrueOrderByCreatedAtDesc();
    }

    public List<Room> getRoomsByHost(String hostId) {
        return roomRepository.findByHostIdOrderByCreatedAtDesc(hostId);
    }

    public Room getRoom(String id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
    }

    public Room createRoom(CreateRoomRequest req, String hostId, String hostName) {
        Room room = new Room();
        room.setName(req.getName());
        room.setMood(req.getMood());
        room.setPublic(req.isPublic());
        room.setHostId(hostId);
        room.setHostName(hostName);
        room.setActive(true);
        room.setCreatedAt(LocalDateTime.now());
        room = roomRepository.save(room);

        PlayerState state = new PlayerState();
        state.setRoomId(room.getId());
        state.setPlaying(false);
        state.setTimestamp(0.0);
        state.setDuration(0.0);
        state.setUpdatedAt(LocalDateTime.now());
        playerStateRepository.save(state);

        return room;
    }

    public void join(String roomId, String userId) {
        getRoom(roomId);
        roomListeners.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(userId, userId);
    }

    public String joinAsGuest(String roomId, String guestName) {
        getRoom(roomId);
        String guestId = "guest_" + UUID.randomUUID().toString().substring(0, 8);
        roomListeners.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(guestId, guestName);
        return guestId;
    }

    public void leave(String roomId, String userId) {
        Map<String, String> listeners = roomListeners.get(roomId);
        if (listeners != null) listeners.remove(userId);
    }

    public List<Map<String, Object>> getListeners(String roomId) {
        Map<String, String> listeners = roomListeners.getOrDefault(roomId, Map.of());
        List<Map<String, Object>> result = new ArrayList<>();
        String[] colors = {"#b8f724","#06b6d4","#f59e0b","#34d399","#f87171","#a78bfa"};
        int i = 0;
        for (Map.Entry<String, String> e : listeners.entrySet()) {
            result.add(Map.of("id", e.getKey(), "name", e.getValue(),
                    "avatar", String.valueOf(e.getValue().charAt(0)).toUpperCase(),
                    "color", colors[i % colors.length], "isHost", false));
            i++;
        }
        return result;
    }

    public void deleteRoom(String roomId, String requesterId) {
        Room room = getRoom(roomId);
        if (!room.getHostId().equals(requesterId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only host can delete");
        room.setActive(false);
        roomRepository.save(room);
    }
}
