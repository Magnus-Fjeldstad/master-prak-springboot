package com.example.waitingroom.controller;

import com.example.waitingroom.service.LobbyService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Controller
public class ViewController {
    @Autowired
    LobbyService lobby;

    @GetMapping("/join")
    public String joinPage(Model model) {
        String qrUrl = "http://localhost:8080/join";
        model.addAttribute("qr", generateQrBase64(qrUrl));
        return "join";
    }

    @PostMapping("/join")
    public String handleJoin(@RequestParam String name) {
        lobby.addPlayer(name);
        return "redirect:/lobby";
    }

    @GetMapping("/lobby")
    public String lobbyView(Model model) {
        model.addAttribute("players", lobby.getAllPlayers());
        return "lobby";
    }

    @GetMapping("/lobby-fragment")
    public String lobbyFragment(Model model) {
        model.addAttribute("players", lobby.getAllPlayers());
        return "lobby :: playerList";
    }

    @GetMapping("/game")
    public String game() {
        return "game";
    }

    private String generateQrBase64(String text) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 200, 200);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            return "";
        }
    }
}
