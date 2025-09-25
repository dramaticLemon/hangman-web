package com.join.tab.controller;

import com.join.tab.application.dto.GameDto;
import com.join.tab.application.service.HangmanGameService;
import com.join.tab.domain.model.valueobject.Language;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;

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
    @GetMapping("/api/game")
    public String index(
            @RequestParam(value = "language", defaultValue = "en") String language,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "difficulty", required = false) String difficulty,
            Model model,
            HttpSession session) {
        try {
            GameDto game;

            if (category != null || difficulty != null) {
                game = gameService.startNewGameWithPreferences(session.getId(), language, category, difficulty);
            } else {
                game = gameService.startNewGameWithLanguage(session.getId(), language);
            }

            model.addAttribute("currentState", game.getCurrentState());
            model.addAttribute("remainingTries", game.getRemainingTries());
            model.addAttribute("status", game.getStatus());
            model.addAttribute("languages", game.getLanguage());
            model.addAttribute("guessedLetters", game.getGuessedLetters());

            if (game.getWord() != null) {
                model.addAttribute("word", game.getWord());
            }

            model.addAttribute("preferences", java.util.Map.of(
                    "language", language,
                    "category", category != null ? category : "any",
                    "difficulty", difficulty != null ? difficulty : "any"
            ));

        } catch (Exception e) {
            log.error("Error loading home page for session {}", session.getId(), e);
            // Fallback to English if there's an error
            try {
                GameDto fallbackGame = gameService.startNewGameWithLanguage(session.getId(), Language.defaultLanguage().getCode());
                model.addAttribute("remainingTries", fallbackGame.getRemainingTries());
                model.addAttribute("status", fallbackGame.getStatus());
                model.addAttribute("language", "en");
                model.addAttribute("error", "Failed to load game in selected language, switched to default");
            } catch (Exception fallbackError) {
                log.error("Even fallback failed for session {}", session.getId(), fallbackError);
                model.addAttribute("error", "Failed to load game");
            }
        }

        return "index";
    }
}
