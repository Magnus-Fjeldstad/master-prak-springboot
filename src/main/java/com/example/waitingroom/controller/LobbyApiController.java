package com.example.waitingroom.controller;

import com.example.waitingroom.model.Player;
import com.example.waitingroom.service.LobbyService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LobbyApiController {
    @Autowired
    LobbyService lobbyService;

    @Operation(summary = "Join the lobby")
    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestParam String name) {
        lobbyService.addPlayer(name);
        return ResponseEntity.ok("Joined");
    }

    @Operation(summary = "Get lobby players")
    @GetMapping("/players")
    public List<Player> getPlayers() {
        return lobbyService.getAllPlayers();
    }

    @GetMapping("/api/status")
    @ResponseBody
    public String getStatus() {
        System.out.println("Game started? " + lobbyService.isGameStarted());
        return String.valueOf(lobbyService.isGameStarted());
    }

    @GetMapping("/api/countdown")
    @ResponseBody
    public String getCountdownSecondsLeft() {
        if (!lobbyService.isCountdownRunning()) {
            return "-1"; // Ikke i gang
        }

        LocalDateTime target = lobbyService.getCountdownStartTime();
        long secondsLeft = java.time.Duration.between(LocalDateTime.now(), target).getSeconds();
        return String.valueOf(Math.max(secondsLeft, 0));
    }


    @PostMapping("/reset")
    public void reset() {
        lobbyService.reset();
    }
}
