package com.join.tab.controller;

import com.join.tab.application.service.WordManagementService;
import com.join.tab.infra.service.WordLoadersService;
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

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadWords(
            @RequestParam("file")MultipartFile file,
            @RequestParam(value = "category", defaultValue = "general") String category) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "File is empty"));
        }

        try {
            WordLoadersService.WordLoadResult result = processUpdatedFile(file, category);

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

    @DeleteMapping("/{wod}")
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

    private WordLoadersService.WordLoadResult processUpdatedFile(
            MultipartFile file, String category) throws IOException {
        WordLoadersService.WordLoadResult result = new WordLoadersService.WordLoadResult();

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
