// QueueItemRepository.java
package com.viberoom.backend.repository;

import com.viberoom.backend.model.QueueItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface QueueItemRepository extends JpaRepository<QueueItem, String> {
    List<QueueItem> findByRoomIdOrderByPositionAsc(String roomId);
    Optional<QueueItem> findByRoomIdAndCurrentTrue(String roomId);
    void deleteByRoomId(String roomId);
}
