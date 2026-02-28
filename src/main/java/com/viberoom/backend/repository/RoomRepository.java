package com.viberoom.backend.repository;

import com.viberoom.backend.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, String> {
    List<Room> findByHostIdOrderByCreatedAtDesc(String hostId);
    List<Room> findByIsPublicTrueAndActiveTrueOrderByCreatedAtDesc();
}
