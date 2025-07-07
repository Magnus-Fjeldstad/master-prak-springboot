package com.example.waitingroom.repository;

import com.example.waitingroom.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {
}