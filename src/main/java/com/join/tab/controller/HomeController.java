package com.join.tab.controller;

import com.join.tab.application.dto.GameDto;
import com.join.tab.application.service.HangmanGameService;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HomeController is responsible for handling requests to the home page ("/api")
 *
 * <p>This Controller:
 * <ul>
 *     <li>Retrieves the current hangman game for the user's session</li>
 *     <li>Starts a new game if none exists</li>
 *     <li>Adds game data (current word state, remaining tries, status) to the model</li>
 *     <li>Returns the "index" view for rendering</li>
 * </ul>
 *
 * It acts as a bridge between the web layer (HTTP request) and the application layer
 * (HangmanGameService).
 */
@Controller
public class HomeController {
    private static final Logger log = LoggerFactory.getLogger(HomeController.class); private final HangmanGameService gameService; /**
     * Creates a new HomeController with a given HangmanService.
     *
     * @param gameService the service that manages Hangman game logic.
     */
    public HomeController(HangmanGameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Handles Get request to the root URL ("/api").
     *
     * <p>It checks is a Hangman game exists for the current HTTP session:
     * <ul>
     *     <li>If yes, it loads the game</li>
     *     <li>If not, it starts a new game</li>
     * </ul>
     *
     * Then it adds game information to the model so the view can display it.
     *
     * @param model the Spring MVC model to pass data to the view.
     * @param session the current HTTP session (used to identity the game)
     * @return the name of view template ("index")
     */
    @GetMapping("/api")
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
