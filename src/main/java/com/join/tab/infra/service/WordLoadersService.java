package com.join.tab.infra.service;

import com.join.tab.infra.entity.WordEntity;
import com.join.tab.infra.repository.jpa.WordJpaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class WordLoadersService {
    private static final Logger log = LoggerFactory.getLogger(WordLoadersService.class);

    private final WordJpaRepository wordJpaRepository;
    private final Pattern validWordPattern = Pattern.compile("^[a-zA-Z]{3,50}$");
    private final Set<String> bannedWords = Set.of(
            "badword1", "badword2"
    );

    public WordLoadersService(WordJpaRepository wordJpaRepository) {
        this.wordJpaRepository = wordJpaRepository;
    }

    /**
     * Loads default word into the database when the application starts.
     *
     * Triggered on {@link ApplicationReadyEvent}.
     * If the database has no active words, it loads words from predefined files
     * and assigns them to appropriate categories.
     * Otherwise, logs the existing number of active words.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void loadWordsOnStartup() {
        if (wordJpaRepository.countByIsActiveTrue() == 0) {
            log.info("No words found in database, loading from file");
            loadWordsFromFile("words/default-words.txt", "general");
            loadWordsFromFile("words/programming-words.txt", "programming");
            loadWordsFromFile("words/animals-words.txt", "animals");

        } else {
            log.info("Words already exists in database : {} active words",
                    wordJpaRepository.countByIsActiveTrue());
        }
    }

    /**
     * Loads word from a file into the database under the specified category
     *
     * Each line in the represents a word. Lines thet are empty or duplicated
     * are skipped. Errors encountered during processing are colleted in the result.
     *
     * This method is transactional, so all successfully processed are persisted
     * automatically.
     *
     * @param filePath the path to the file on the classpath
     * @param category the category to assign to the words
     * @return a {@link WordLoadResult} containing counts of loaded, skipped words and any errors
     */
    @Transactional
    public WordLoadResult loadWordsFromFile(String filePath, String category) {
        WordLoadResult result = new WordLoadResult();

        try {
            ClassPathResource resource = new ClassPathResource(filePath);

            if (!resource.exists()) {
                log.warn("Word file not found: {}", filePath);
                result.addError("File not found: " + filePath);

                return  result;
            }

            Set<String> processWords = new HashSet<>();

            try (InputStream inputStream = resource.getInputStream();
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                String line;
                int lineNumber = 0;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    processWord(line.trim(), category, lineNumber, processWords, result);
                }
            }

           log.info("Word loading completed for: {}: {} loaded, {} skipped, {} errors",
                   filePath, result.getLoadedCount(), result.getSkippedCount(), result.getErrors().size());

        } catch (IOException e) {
            log.error("Error loading words form file: {}", filePath, e);
            result.addError("IO Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error loading words from file: {}", filePath, e);
            result.addError("Unexpected error: " + e.getMessage());
        }

        return result;
    }

    /**
     * Processes a single word from a file and attempts to save in the database.
     *
     * The method performs the following checks:
     * - Skips empty lines and lines starting with '#'.
     * - Validates word format (alphabetic only, proper length).
     * - Skips duplicates within the current batch.
     * - Skips words already present in the database.
     * - Skips banned words.
     *
     * Successfully validated words are saved as {@link WordEntity} in the database.
     * Updates the provided {@link  WordLoadResult} with counts of loaded, skipped words and errors.
     *
     * @param word the word to process
     * @param category the category to assign to the word
     * @param lineNumber the line number in the source file (used for error reporting)
     * @param processedWords a set of words already processed in the current batch
     * @param result the {@link WordLoadResult} object to track success, skips, and errors
     */
    private void processWord(String word, String category, int lineNumber,
                             Set<String> processedWords, WordLoadResult result) {
        try {
            if (word.isEmpty() || word.startsWith("#")) {
                return; // skip empty lines ans comments
            }

            String cleanWord = word.toLowerCase().trim();

            // Validate word format
            if (!isValidWord(cleanWord)) {
                result.addError("Line " + lineNumber + ": Invalid word format: " + word);
                result.incrementSkipped();
                return;
            }

            // Check for duplicates in current batch
            if (processedWords.contains(cleanWord)) {
                result.incrementLoaded();
                return;
            }

            // Check if word already exists in database
            if (wordJpaRepository.findByValueIgnoreCase(cleanWord).isPresent()) {
                result.incrementSkipped();
                return;
            }

            // Check banned words
            if (bannedWords.contains(cleanWord)) {
                result.addError("Lina " + lineNumber + ": Banned word: " + word);
                result.incrementSkipped();
                return;
            }


            // Create and save word entity
            WordEntity entity = createWordEntity(cleanWord, category);
            wordJpaRepository.save(entity);

            processedWords.add(cleanWord);
            result.incrementLoaded();
        } catch (Exception e) {
            log.error("Error processing word '{}' at line '{}'", word, lineNumber, e);
            result.addError("Line " + lineNumber + ": Error processing '" + word + "': " + e.getMessage());
        }
    }

    /**
     * Check if a word is valid based on format and length.
     *
     * A valid word:
     * - is not null
     * - matches the allowed pettern (letters only)
     * - has length between 3 and 50 char
     * @param word the word to validate
     * @return {@code true} if the word is valid, {@code false} otherwise
     */
    private boolean isValidWord(String word) {
        return word != null
                && validWordPattern.matcher(word).matches()
                && word.length() >= 3
                && word.length() <= 50;
    }

    /**
     * Creates a new {@link WordEntity} for persistence.
     *
     * Sets the word value, category, and marks it as active.
     * The length and difficulty level will be automatically set by
     * the {@code @PrePersist} method in {@link WordEntity}.
     *
     * @param word the word value
     * @param category the category to assign
     * @return a new {@link WordEntity} ready for saving
     */
    private WordEntity createWordEntity(String word, String category) {
        WordEntity entity = new WordEntity();
        entity.setValue(word);
        entity.setCategory(category);
        entity.setIsActive(true);
        // Length and difficulty will be set by @PrePersist

        return entity;
    }

    /**
     * Reloads words from predefined files into the database.
     *
     * The method performs the following steps:
     * 1. Deactivates all existing words in the database.
     * 2. Loads fresh words from default, programming, and animals word files.
     *
     * This method is transactional, so all changes are applied atomically.
     * It ensures the database has an up-to-date set of words while preserving
     * inactive words for historical or rollback purposes.
     */
    @Transactional
    public void reloadWordFromFiles() {
        log.info("Reloading word from files...");

        // Deactivate all existing words
        List<WordEntity> allWords = wordJpaRepository.findAll();
        allWords.forEach(word -> word.setIsActive(false));
        wordJpaRepository.saveAll(allWords);

        // Load fresh words
        loadWordsFromFile("words/default-words.txt", "general");
        loadWordsFromFile("words/programming-words.txt", "programming");
        loadWordsFromFile("words/animals-words.txt", "animals");

    }

    /**
     * Represents the result of loading words from a file or source.
     *
     * Tracks the number of successfully loaded words, skipped words,
     * and any errors encountered during processing.
     */
    public static class WordLoadResult {
        private int loadedCount = 0;
        private int skippedCount = 0;
        private final List<String> errors = new ArrayList<>();

        public void incrementLoaded() { loadedCount++; }
        public void incrementSkipped() { skippedCount++; }
        public void addError(String error) { errors.add(error); }

        public int getLoadedCount() {
            return loadedCount;
        }

        public int getSkippedCount () {
            return skippedCount;
        }

        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }
    }

}
