package com.join.tab.controller;

import com.join.tab.domain.exception.GameNotFoundException;
import com.join.tab.domain.exception.LetterAlreadyGuessedException;
import com.join.tab.application.dto.GameDto;
import com.join.tab.application.dto.GuessDto;
import com.join.tab.application.service.HangmanGameService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/hangman")
public class HangmanController {

    private final HangmanGameService gameService;

    public HangmanController(HangmanGameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startGame(HttpSession session) {
        try {
            GameDto game = gameService.startNewGame(session.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("currentState", game.getCurrentState());
            response.put("remainingTries", game.getRemainingTries());
            response.put("status", game.getStatus());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to start game"));
        }
    }

    @PostMapping("/guess")
    public ResponseEntity<Map<String, Object>> guessLetter(
            @RequestParam char letter,
            HttpSession session) {

        try {
            GuessDto result = gameService.guessLetter(session.getId(), letter);

            Map<String, Object> response = new HashMap<>();
            response.put("currentState", result.getCurrentState());
            response.put("remainingTries", result.getRemainingTries());
            response.put("status", result.getStatus());
            response.put("wasCorrect", result.wasCorrect());

            if (result.getWord() != null) {
                response.put("word", result.getWord());
            }

            return ResponseEntity.ok(response);

        } catch (GameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Game not found. Please start a new game."));
        } catch (LetterAlreadyGuessedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process guess"));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getGameStatus(HttpSession session) {
        try {
            GameDto game = gameService.getCurrentGame(session.getId());

            if (game == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No active game found"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("currentState", game.getCurrentState());
            response.put("remainingTries", game.getRemainingTries());
            response.put("status", game.getStatus());

            if (game.getWord() != null) {
                response.put("word", game.getWord());
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get game status"));
        }
    }

    @DeleteMapping("/end")
    public ResponseEntity<Map<String, Object>> endGame(HttpSession session) {
        try {
            gameService.endGame(session.getId());
            return ResponseEntity.ok(Map.of("message", "Game ended successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to end game"));
        }
    }
}
