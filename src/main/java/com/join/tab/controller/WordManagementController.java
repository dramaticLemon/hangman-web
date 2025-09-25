package com.join.tab.controller;

import com.join.tab.application.service.WordManagementService;
import com.join.tab.infra.service.WordLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/words")
public class WordManagementController {

    private static final Logger log = LoggerFactory.getLogger(WordManagementController.class);

    private final WordManagementService wordManagementService;

    public WordManagementController (WordManagementService wordManagementService) {
        this.wordManagementService = wordManagementService;
    }

    /**
     * Retrieves statistics about the word available int Hangman game.
     *
     * Steps performed:
     * 1. Gets the total number of word via {@link WordManagementService#getWordCount()}.
     * 2. Gets the list of available categories via {@link WordManagementService#getAvailableCategories()}.
     * 3. Returns HTTP 200 ok with the statistics if successful.
     * 4. Returns HTTP 500 Internal Server Error with an error message if an unexpected error message
     *
     * @return a {@link ResponseEntity} containing word statistics or an error message
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getWordStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalWords", wordManagementService.getWordCount());
            stats.put("categories", wordManagementService.getAvailableCategories());

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Failed to ge word statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get word statistics"));
        }
    }

    /**
     * Uploads a file containing words to add tho the Hangman game.
     *
     * Steps performed:
     * 1. Check if the uploaded file is empty and return HTTP 400 Bad Request if it is.
     * 2. Process the file and loads words into the specified category using {@link WordLoaderService}.
     * 3. Prepares a response containing:
     *  - "loaded": number of words successfully loaded
     *  - "skipped: number of words skipped
     *  - "error: any errors encountered during processing
     *  - "success": whether the upload was successful
     * 4. Returns HTTP 200 ok with the upload result if successful.
     * 5. Returns HTTP 500 Internal Server Error with an error message if an unexpected error occurs.
     *
     * @param file the file containing word to upload
     * @param category the category to assign the uploaded words (default is "general")
     * @return a {@link ResponseEntity} containing the upload result or an error message
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadWords(
            @RequestParam("file")MultipartFile file,
            @RequestParam(value = "category", defaultValue = "general") String category) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "File is empty"));
        }

        try {
            WordLoaderService.WordLoadResult result = processUpdatedFile(file, category);

            Map<String, Object> response = new HashMap<>();
            response.put("loaded", result.getLoadedCount());
            response.put("skipped", result.getSkippedCount());
            response.put("error", result.getErrors());
            response.put("success", !result.hasErrors());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to upload words form file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process uploaded file"));
        }
    }

    @PostMapping("/{language}/upload-words")
    public ResponseEntity<Map<String, Object>> uploadWordsForLanguage(
            @PathVariable String language,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", defaultValue = "general") String category) {

        try {
            // This would require extending WordManagementService to handle language-specific uploads
            WordLoaderService.WordLoadResult result = wordManagementService.loadWordsFromFile(
                    file.getOriginalFilename(), category);

            Map<String, Object> response = new HashMap<>();
            response.put("language", language);
            response.put("loaded", result.getLoadedCount());
            response.put("skipped", result.getSkippedCount());
            response.put("errors", result.getErrors());
            response.put("success", !result.hasErrors());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to upload words for language: {}", language, e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to upload words for " + language));
        }
    }

    /**
     * Add a single word to the Hangman game repository.
     *
     * Steps performed:
     * 1. Uses {@link WordManagementService#addWord(String, String)} to add the word to the specified category.
     * 2. Returns HTTP 200 ok with a success message if the word was added successfully.
     * 3. Returns HTTP 400 Bad Request if the word is invalid or already exists.
     * 4. Returns HTTP 500 Internal Server Error with an error message if an unexpected error occur.
     *
     * @param word the word to add
     * @param category the category to assign the word (default is "general")
     * @return a {@link ResponseEntity} containing the result of the operation
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addWord(
            @RequestParam String word,
            @RequestParam(value = "category", defaultValue = "general") String category){

        try {
            boolean success = wordManagementService.addWord(word, category);

            if (success) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Word added successfully"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Failed to add word (invalid or already exists"
                ));

            }
        } catch (Exception e){
           log.error("Failed to add word: {}", word, e);
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body(Map.of("error", "Failed to add word"));
        }
    }

    @PostMapping("/{language}/add-word")
    public ResponseEntity<Map<String, Object>> addWordForLanguage(
            @PathVariable String language,
            @RequestParam String word,
            @RequestParam(value = "category", defaultValue = "general") String category) {

        try {
            // This would require extending WordManagementService to handle language-specific word addition
            boolean success = wordManagementService.addWord(word, category);

            return ResponseEntity.ok(Map.of(
                    "success", success,
                    "language", language,
                    "word", word,
                    "message", success ? "Word added successfully" : "Failed to add word"
            ));

        } catch (Exception e) {
            log.error("Failed to add word '{}' for language: {}", word, language, e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to add word"));
        }
    }

    /**
     * Removes a word from the Hangman game dictionary
     *
     * Steps performed:
     * 1. Uses {@link WordManagementService#removeWord(String)} to remove the specified word.
     * 2. Returns HTTP 200 ok with a success message if the word was removed successfully.
     * 3. Returns HTTP 400 Bad Request if the word was not found.
     * 4. Returns HTTP 500 Internal Server error with an error message if an unexpected error occurs.
     *
     * @param word the word to remove
     * @return a {@link ResponseEntity} containing the result of the operation
     */
    @DeleteMapping("/{word}")
    public ResponseEntity<Map<String, Object>> removeWord(
            @PathVariable String word) {
        try {
            boolean success = wordManagementService.removeWord(word);

            if (success) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Word removed successfully"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Word not found"
                ));
            }
        } catch (Exception e) {
            log.error("Failed to remove word: {}", word, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to remove word"));
        }
    }

    /**
     * Reloads all words in the Hangman game dictionary.
     *
     * Steps performed:
     * 1. Call {@link WordManagementService#reloadAllWords()} to refresh the word list.
     * 2. 200 ok with a success message and the total number of word after reload.
     * 3. 500 Internal Server Error with an error message if an unexpected error occur.
     *
     * @return a {@link ResponseEntity} containing the reload result and total word count or an error message
     */
    @PostMapping("/reload")
    public ResponseEntity<Map<String, Object>> reloadWords() {
        try {
            wordManagementService.reloadAllWords();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Words reloaded successfully",
                    "totalWords", wordManagementService.getWordCount()
            ));
        } catch (Exception e) {
            log.error("Failed to reload words", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reload words"));
        }
    }

    /**
     * Checks if a word exists in the Hangman game dictionary.
     *
     * Steps performed:
     * 1. Uses {@link WordManagementService#wordExists(String)} to check if the word is present.
     * 2. 200 ok with the word and a boolean indicating existence.
     * 3. 500 Internal Server Error with an error message if an unexpected error occur.
     *
     * @param word the word to check.
     * @return a {@link ResponseEntity} containing the word and its existence status or an error message.
     */
    @GetMapping("/exists/{word}")
    public ResponseEntity<Map<String, Object>> checkWordExists(@PathVariable String word) {
        try {
            boolean exists = wordManagementService.wordExists(word);

            return ResponseEntity.ok(Map.of(
                    "word", word,
                    "exists", exists
            ));
        } catch (Exception e) {
            log.error("Failed to check if word exists: {}", word, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to check word existence"));
        }
    }

    /**
     * Process an uploaded file containing word and adds them to the specified category.
     *
     * Steps performed:
     * 1. Reads the uploaded file line by line using UTF-8 encoding.
     * 2. Ignores empty lines and lines starting with "#" (treated as comments).
     * 3. Trims each line and attempts to add as a word to the specified category via {@link WordManagementService#addWord(String, String)}
     * 4. Tracks the number of words successfully loaded adn skipped.
     * 5. Returns a {@link WordLoaderService.WordLoadResult} containing the result.
     *
     * @param file the uploaded file containing words.
     * @param category the category to assign the words.
     * @return a {@link WordLoaderService.WordLoadResult} with counts of loaded and skipped words
     * @throws IOException if an I/O error occurs while reading the file.
     */
    private WordLoaderService.WordLoadResult processUpdatedFile(
            MultipartFile file, String category) throws IOException {
        WordLoaderService.WordLoadResult result = new WordLoaderService.WordLoadResult();

        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String word = line.trim();

                if(!word.isEmpty() && !word.startsWith("#")) {
                    if (wordManagementService.addWord(word, category)) {
                        result.incrementLoaded();
                    } else {
                        result.incrementSkipped();
                    }
                }
            }
        }

        return result;
    }
}
