package com.join.tab.controller;

import com.join.tab.application.dto.LanguageInfoDto;
import com.join.tab.domain.exception.GameNotFoundException;
import com.join.tab.domain.exception.LetterAlreadyGuessedException;
import com.join.tab.application.dto.GameDto;
import com.join.tab.application.dto.GuessDto;
import com.join.tab.application.service.HangmanGameService;
import com.join.tab.domain.exception.UnsupportedLanguageException;
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
    public ResponseEntity<Map<String, Object>> startGame(
            @RequestParam(value = "lang", defaultValue = "en") String language,
            HttpSession session) {

        try {
            GameDto game = gameService.startNewGameWithLanguage(
                    session.getId(), language);

            Map<String, Object> response = createGameResponse(game);
            response.put("message", "Game started successfully");
            log.info("New game started for session {} with languages {}", session.getId(), language);

            return ResponseEntity.ok(response);

        } catch (UnsupportedLanguageException e) {
            log.warn("Attempted to start game with unsupported language: {}", language);
            return ResponseEntity.badRequest().body(Map.of("error", "Unsupported language: " + language));

        } catch (Exception e) {
            log.error("Failed to start game for session {}", session.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to start game"));
        }
    }

    @PostMapping("/start-with-preferences")
    public ResponseEntity<Map<String, Object>> startGameWithPreferences(
            @RequestParam(value = "language", defaultValue = "en") String language,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "difficult", required = false) String difficult,
            HttpSession session) {
        try {
            GameDto game = gameService.startNewGameWithPreferences(
                    session.getId(), language, category, difficult
            );
            Map<String, Object> response = createGameResponse(game);
            response.put("message", "Game started with preferences");
            response.put("preferences", Map.of(
                    "language", language,
                    "category", category != null ? category : "any",
                    "difficulty", difficult != null ? difficult: "any"
            ));

            log.info("New game started for session {} with preferences: lang={} ,cat={}, diff={}",
                    session.getId(), language, category, difficult);

            return ResponseEntity.ok(response);

        } catch (UnsupportedLanguageException e) {
            log.warn("Attempted to start-with-preferences game with unsupported language: {}", language);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Unsupported language: " + language));

        } catch (IllegalArgumentException e) {
            log.warn("Invalid game preferences provided: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid preferences: " + e.getMessage()));

        } catch (Exception e) {
            log.error("Failed to start game with preferences for session {}", session.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to start game"));
        }
    }

    private Map<String, Object> createGameResponse(GameDto game) {
        Map<String, Object> response = new HashMap<>();
        response.put("currentState", game.getCurrentState());
        response.put("remainingTries", game.getRemainingTries());
        response.put("status", game.getStatus());
        response.put("language", game.getLanguage());

        if (game.getCategory() != null) {
            response.put("category", game.getCategory());
        }

        if (game.getWord() != null) {
            response.put("word", game.getWord());
        }

        return response;
    }

    private Map<String, Object> createGuessResponse(GuessDto result) {
        Map<String, Object> response = new HashMap<>();
        response.put("currentState", result.getCurrentState());
        response.put("remainingTries", result.getRemainingTries());
        response.put("status", result.getStatus());
        response.put("language", result.getLanguage());
        response.put("wasCorrect", result.isWasCorrect());

        if (result.getWord() != null) {
            response.put("word", result.getWord());
        }

        return response;
    }

    /**
     * Processes a letter guess for the current Hangman game associated with the HTTp session.
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

            Map<String, Object> response = createGuessResponse(result);

            return ResponseEntity.ok(response);

        } catch (GameNotFoundException e) {
            log.warn("Guess attempted for non-existent game, session: {}", session.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Game not found. Please start a new game."));

        } catch (LetterAlreadyGuessedException e) {
            log.info("Duplicate letter guess: {} for session {}", letter, session.getId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));

        } catch (IllegalArgumentException e) {
            log.warn("Invalid letter '{}' guessed for session {}: {}", letter, session.getId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid letter for this language: " + letter));

        } catch (Exception e) {
            log.error("Failed to process guess for session {}", session.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process guess"));
        }
    }

    /**
     * Retrieves the current status of the Hangman game for the HTTP session.
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

            Map<String, Object> response = createGameResponse(game);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to get game status for session: {}", session.getId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get game status"));
        }
    }

    /**
     * Ends the current Hangman game for the HTTP session.
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
            log.error("Failed to end game for session: {}", session.getId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to end game"));
        }
    }

    /**
     * Handles GET request to retrieve supported language and their related data.
     * <p>Returns a json response with</p>
     * <ul>
     *     <li><b>supportedLanguages</b> - list of available language codes</li>
     *     <li><b>languagesData</b> - detailed information per language</li>
     * </ul>
     *
     * @return {@link ResponseEntity} containing:
     *      <ul>
     *          <li>HTTP 200 with a map of supported languages and language data</li>
     *          <li>HTTP 500 with an error message if retrieval fails</li>
     *      </ul>
     */
    @GetMapping("/languages")
    public ResponseEntity<Map<String, Object>> getSupportedLanguages() {
        try {
            LanguageInfoDto languagesInfo = gameService.getAllLanguagesInfo();

            Map<String, Object> response = new HashMap<>();
            response.put("supportedLanguages", languagesInfo.getAllLanguages());
            response.put("languagesData", languagesInfo.getLanguagesData());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to get supported languages", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get language information"));
        }
    }

   /** Retrieves detailed information about a specific language by its code.
    * <p>Response JSON includes:
    * <ul>
    * <li><b>language</b> – language code</li>
    *   <li><b>displayName</b> – human-readable language name</li>
    *   <li><b>categories</b> – list of available categories</li>
    *   <li><b>wordCount</b> – total number of words</li>
    *   <li><b>supported</b> – whether the language is supported</li>
    * </ul>
    *
    * @param languageCode the ISO code of the language
    * @return {@link ResponseEntity} with:
    *         <ul>
    *           <li>HTTP 200 (OK) and language info if found</li>
    *           <li>HTTP 404 (Not Found) if the language is unsupported</li>
    *           <li>HTTP 500 (Internal Server Error) on unexpected errors</li>
    *         </ul>
    */
    @GetMapping("/languages/{languageCode}")
    public ResponseEntity<Map<String, Object>> getLanguageInfo(@PathVariable String languageCode) {
        try {
            LanguageInfoDto languageInfo = gameService.getLanguageInfo(languageCode);

            Map<String, Object> response = new HashMap<>();
            response.put("language", languageInfo.getLanguageCode());
            response.put("displayName", languageInfo.getDisplayName());
            response.put("categories", languageInfo.getCategories());
            response.put("wordCount", languageInfo.getWordCount());
            response.put("supported", languageInfo.isSupported());

            return ResponseEntity.ok(response);

        } catch (UnsupportedLanguageException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Language not supported: " + languageCode));
        } catch (Exception e) {
            log.error("Failed to get language info for: {}", languageCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get language information"));
        }
    }

}
