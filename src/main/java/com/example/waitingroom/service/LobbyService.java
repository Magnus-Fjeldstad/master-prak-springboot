package com.example.waitingroom.service;

import com.example.waitingroom.model.Player;
import com.example.waitingroom.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

@Service
public class LobbyService {

    private final PlayerRepository playerRepo;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> countdownTask;
    private boolean gameStarted = false;
    private LocalDateTime countdownStartTime;

    private static final int MAX_PLAYERS = 4;
    private static final int MIN_PLAYERS = 2;

    @Autowired
    public LobbyService(PlayerRepository repo) {
        this.playerRepo = repo;
    }

    public synchronized void addPlayer(String name) {
        if (playerRepo.count() >= MAX_PLAYERS || gameStarted)
            return;

        Player p = new Player();
        p.setName(name);
        p.setJoinedAt(LocalDateTime.now());
        playerRepo.save(p);

        long count = playerRepo.count();

        if (count >= MIN_PLAYERS && countdownTask == null) {
            startCountdown();
        }

        if (count >= MAX_PLAYERS && !gameStarted) {
            startGame(5); // start tidligere om fullt
        }
    }

    private void startCountdown() {
        countdownStartTime = LocalDateTime.now().plusSeconds(10); // Endret fra 40 til 10
        countdownTask = scheduler.schedule(() -> startGame(0), 10, TimeUnit.SECONDS);
    }

    private void startGame(int delaySeconds) {
        scheduler.schedule(() -> {
            gameStarted = true;
            System.out.println("GAME STARTED!");
            countdownTask = null;
        }, delaySeconds, TimeUnit.SECONDS);
    }

    public List<Player> getAllPlayers() {
        return playerRepo.findAll();
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public LocalDateTime getCountdownStartTime() {
        return countdownStartTime;
    }

    public boolean isCountdownRunning() {
        return countdownTask != null && !gameStarted;
    }

    public void reset() {
        playerRepo.deleteAll();
        gameStarted = false;
        countdownStartTime = null;
        if (countdownTask != null) {
            countdownTask.cancel(true);
            countdownTask = null;
        }
    }
}
