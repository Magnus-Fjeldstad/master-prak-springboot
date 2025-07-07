package com.example.waitingroom.service;

import com.example.waitingroom.model.Player;
import com.example.waitingroom.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class LobbyService {
    private final PlayerRepository playerRepo;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> countdownTask;
    private boolean gameStarted = false;

    private static final int MAX_PLAYERS = 4;
    private static final int MIN_PLAYERS = 2;

    @Autowired
    public LobbyService(PlayerRepository repo) {
        this.playerRepo = repo;
    }

    public synchronized void addPlayer(String name) {
        if (playerRepo.count() >= MAX_PLAYERS) return;

        Player p = new Player();
        p.setName(name);
        p.setJoinedAt(LocalDateTime.now());
        playerRepo.save(p);

        long count = playerRepo.count();
        if (count >= MIN_PLAYERS && countdownTask == null) {
            startCountdown();
        }

        if (count >= MAX_PLAYERS && !gameStarted) {
            startGame(5); // Start within 5s
        }
    }

    private void startCountdown() {
        countdownTask = scheduler.schedule(() -> startGame(0), 60, TimeUnit.SECONDS);
    }

    private void startGame(int delaySeconds) {
        scheduler.schedule(() -> {
            gameStarted = true;
            System.out.println("GAME STARTED!");
            playerRepo.deleteAll(); // Reset for demo
            countdownTask = null;
        }, delaySeconds, TimeUnit.SECONDS);
    }

    public List<Player> getAllPlayers() {
        return playerRepo.findAll();
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void reset() {
        playerRepo.deleteAll();
        gameStarted = false;
        if (countdownTask != null) {
            countdownTask.cancel(true);
            countdownTask = null;
        }
    }
}