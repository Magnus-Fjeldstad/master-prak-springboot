package com.example.waitingroom.controller;

import com.example.waitingroom.model.Player;
import com.example.waitingroom.service.LobbyService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LobbyApiController {
    @Autowired
    LobbyService lobby;

    @Operation(summary = "Join the lobby")
    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestParam String name) {
        lobby.addPlayer(name);
        return ResponseEntity.ok("Joined");
    }

    @Operation(summary = "Get lobby players")
    @GetMapping("/players")
    public List<Player> getPlayers() {
        return lobby.getAllPlayers();
    }

    @Operation(summary = "Check if game started")
    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of("started", lobby.isGameStarted());
    }

    @PostMapping("/reset")
    public void reset() {
        lobby.reset();
    }
}
