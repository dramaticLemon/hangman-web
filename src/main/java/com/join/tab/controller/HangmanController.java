package com.join.tab.controller;

import com.join.tab.domain.exception.GameNotFoundException;
import com.join.tab.domain.exception.LetterAlreadyGuessedException;
import com.join.tab.application.dto.GameDto;
import com.join.tab.application.dto.GuessDto;
import com.join.tab.application.service.HangmanGameService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/hangman")
public class HangmanController {
    private final static Logger log = LoggerFactory.getLogger(HangmanController.class);
    private final HangmanGameService gameService;

    public HangmanController(HangmanGameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Start a new Hangman game for the current HTTP session.
     *
     * Steps performed:
     * 1. Uses the session ID to start a new game via the {@link HangmanGameService}.
     * 2. Prepares a response containing:
     *      - "currentState": the current state of the word being guessed
     *      - "remainingTries": number of remaining guesses
     *      - "status": current game status
     * 3. Returns HTTP 200 ok with the game details is successful.
     * 4. Returns HTTP 500 Internal Server Error with an error message if something goes wrong.
     *
     *
     * @param session the current HTTP session.
     * @return a {@link ResponseEntity} containing game details or an error message.
     */
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

    /**
     * Processes a letter guess for the current Hangman game associated with the HTTp session.
     *
     * Steps performed:
     * 1. Retrieves the session ID and guessed letter form the request.
     * 2. Calls {@link HangmanGameService#guessLetter(String, char)} to process the guess.
     * 3. Prepares a response containing:
     *  - "currentState": the current state of the word being guessed
     *  - "remainingTries": number of remaining guesses
     *  - "status" current game status
     *  - "wasCorrect": whether the guess was correct
     *  - "word" the complete word if the game is correct
     * 4. Returns HTTP 200 OK with the guess result if successful.
     * 5. Returns HTTP 404 Not Found if no game exists for the session.
     * 6. Returns HTTP 400 Bad Request if the letter was already guessed.
     * 7. Returns HTTP 500 Internal Server Error if an unexpected error occurs.
     *
     * @param letter the letter being guessed.
     * @param session the current HTTP session.
     * @return a {@link ResponseEntity} containing the guess result or an error message.
     */
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
            response.put("wasCorrect", result.isWasCorrect());

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

    /**
     * Retrieves the current status of the Hangman game for the HTTP session.
     *
     * Steps performed:
     * 1: Uses the session ID to get the current game via {@link HangmanGameService#getCurrentGame(String)}.
     * 2. If no game exists, returns HTTP 404 Not Found with an error message.
     * 3. Prepares a response containing:
     *  - "currentState": the current state of the word being guessed
     *  - "remainingTries": number of remaining guessed
     *  - "status: current game status
     *  - "word: the complete word if the game is finished
     *  4. Returns HTTP 200 OK with the game status if successful.
     *  5. Returns HTTP 500 Internal Server Error is an unexpected error occurs.
     *
     * @param session the current HTTP session
     * @return a {@link ResponseEntity} containing the game status or an error message
     */
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

    /**
     * Ends the current Hangman game for the HTTP session.
     *
     * Steps performed:
     * 1. Uses the session ID to end the game via {@link HangmanGameService#endGame(String)}.
     * 2. Returns HTTP 200 ok with a success message if the game is ended successfully.
     * 3, Returns HTTP 500 Internal Server Error with an error message if an unexpected error message.
     *
     * @param session the current HTTP session
     * @return a {@link ResponseEntity} containing a success message or an error message
     */
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
