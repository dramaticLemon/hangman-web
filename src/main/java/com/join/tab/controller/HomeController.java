package com.join.tab.controller;

import com.join.tab.application.dto.GameDto;
import com.join.tab.application.service.HangmanGameService;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final HangmanGameService gameService;

    public HomeController(HangmanGameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        GameDto game = gameService.getCurrentGame(session.getId());

        if (game == null) {
            game = gameService.startNewGame(session.getId());
        }

        model.addAttribute("currentState", game.getCurrentState());
        model.addAttribute("remainingTries", game.getRemainingTries());
        model.addAttribute("status", game.getStatus());

        return "index";
    }
}
