package com.viberoom.backend.repository;

import com.viberoom.backend.model.PlayerState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerStateRepository extends JpaRepository<PlayerState, String> {
}
