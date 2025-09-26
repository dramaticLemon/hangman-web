package com.join.tab.infra.service;

import com.join.tab.domain.valueobject.Language;
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
import java.util.*;
import java.util.regex.Pattern;

@Service
public class WordLoaderService {
    private static final Logger log = LoggerFactory.getLogger(WordLoaderService.class);

    // interface
    private final WordJpaRepository wordJpaRepository;
    // Language-specific validation patterns
    private final Map<String, Pattern> validationPatterns = Map.of(
            "en", Pattern.compile("^[a-zA-Z]{3,50}$"),
            "ua", Pattern.compile("^[А-ЯІЇЄҐа-яіїєґ]{3,50}$"),
            "de", Pattern.compile("^[a-zA-ZäöüÄÖÜß]{3,50}$"),
            "fr", Pattern.compile("^[a-zA-ZàâäéèêëïîôöùûüÿçÀÂÄÉÈÊËÏÎÔÖÙÛÜŸÇ]{3,50}$"),
            "es", Pattern.compile("^[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ]{3,50}$")
    );
    // Language-specific banned words
    private final Map<String, Set<String>> bannedWords = Map.of(
            "en", Set.of("badword", "inappropriate"),
            "ru", Set.of("поганеслово"),
            "de", Set.of("schlechtesWort"),
            "fr", Set.of("motinterdit"),
            "es", Set.of("palabramala")
    );

    public WordLoaderService (WordJpaRepository wordJpaRepository) {
        this.wordJpaRepository = wordJpaRepository;
    }

    /**
     * Loads default word into the database when the application starts.
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

            loadDefaultWords();

        } else {
            log.info("Words already exists in database : {} active words",
                    wordJpaRepository.countByIsActiveTrue());
        }
    }

    private void loadDefaultWords() {
        // load English words
        loadWordsFromFile("words/english/general-words.txt", "en", "general");
        loadWordsFromFile("words/english/programming-words.txt", "en", "programming");
        loadWordsFromFile("words/english/animals-words.txt", "en", "animals");
        loadWordsFromFile("words/english/technology-words.txt", "en", "technology");

        // Load Ukrainian words
        loadWordsFromFile("words/ukrainian/general-words.txt", "ua", "general");
        loadWordsFromFile("words/ukrainian/programming-words.txt", "ua", "programming");
        loadWordsFromFile("words/ukrainian/animals-words.txt", "ua", "animals");
        loadWordsFromFile("words/ukrainian/technology-words.txt", "ua", "technology");

        logLanguageStatistics();
    }

    /**
     * Loads word from a file into the database under the specified category
     * Each line in the represents a word. Lines thet are empty or duplicated
     * are skipped. Errors encountered during processing are colleted in the result.
     * This method is transactional, so all successfully processed are persisted
     * automatically.
     *
     * @param filePath the path to the file on the classpath
     * @param category the category to assign to the words
     * @param language the language word file
     * @return a {@link WordLoadResult} containing counts of loaded, skipped words and any errors
     */
    @Transactional
    public WordLoadResult loadWordsFromFile(
            String filePath, String language, String category) {
        WordLoadResult result = new WordLoadResult(language, category);

        try {

            // validate language
            new Language(language);

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
                    processWord(line.trim(), language, category, lineNumber, processWords, result);
                }
            }

           log.info("Word loading completed for: {}: {} loaded, {} skipped, {} errors",
                   filePath, result.getLoadedCount(), result.getSkippedCount(), result.getErrors().size());
        } catch (IllegalArgumentException e) {
            log.error("Invalid language: {}", language, e);
            result.addError("Invalid language: " + language);
        } catch (IOException e) {
            log.error("Error loading words form file: {}", filePath, e);
            result.addError("IO Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error loading words from file: {}", filePath, e);
            result.addError("Unexpected error: " + e.getMessage());
        }

        return result;
    }

    @Transactional
    public WordLoadResult loadWordsFromStream(
            InputStream inputStream, String language, String category) {

        WordLoadResult result = new WordLoadResult(language, category);
        try {
            // validate language
            new Language(language);

            Set<String> processedWords = new HashSet<>();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                String line;
                int lineNumber = 0;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    processWord(line.trim(), language, category, lineNumber, processedWords, result);
                }
            }

            log.info("Word loading completed for language {}: {} loaded, {} skipped, {} errors",
                    language, result.getLoadedCount(), result.getSkippedCount(), result.getErrors().size());

        } catch (IllegalArgumentException e) {
            log.error("Invalid language: {}", language, e);
            result.addError("Invalid language: " + language);
        } catch (IOException e) {
            log.error("Error reading uploaded file for language: {}", language, e);
            result.addError("IO Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error loading words for language: {}", language, e);
            result.addError("Unexpected error: " + e.getMessage());
        }

        return result;
    }

    /**
     * Processes a single word from a file and attempts to save in the database.
     * The method performs the following checks:
     * - Skips empty lines and lines starting with '#'.
     * - Validates word format (alphabetic only, proper length).
     * - Skips duplicates within the current batch.
     * - Skips words already present in the database.
     * - Skips banned words.
     * Successfully validated words are saved as {@link WordEntity} in the database.
     * Updates the provided {@link  WordLoadResult} with counts of loaded, skipped words and errors.
     *
     * @param word the word to process
     * @param category the category to assign to the word
     * @param lineNumber the line number in the source file (used for error reporting)
     * @param processedWords a set of words already processed in the current batch
     * @param result the {@link WordLoadResult} object to track success, skips, and errors
     */
    private void processWord(
            String word, String language, String category, int lineNumber,
            Set<String> processedWords, WordLoadResult result) {
        try {

            // skip empty lines and comments
            if (word.isEmpty() || word.startsWith("#")) {
                return;
            }

            String cleanWord = word.toLowerCase().trim();

            // Validate word format for specific language
            if (!isValidWordForLanguage(cleanWord, language)) {
                result.addError("Line " + lineNumber + ": Invalid word format for language: " + language + ": " + word);
                result.incrementSkipped();
                return;
            }

            // Check for duplicates in current batch
            if (processedWords.contains(cleanWord)) {
                result.incrementLoaded();
                return;
            }

            // Check if word already exists in database
            if (wordJpaRepository.findByValueIgnoreCaseAndLanguage(cleanWord, language).isPresent()) {
                result.incrementSkipped();
                return;
            }

            // Check banned words
            if (isBannedWord(cleanWord, language)) {
                result.addError("Line " + lineNumber + ": Banned word for " + language + ": " + word);
                result.incrementSkipped();
                return;
            }

            // Create and save word entity
            WordEntity entity = createWordEntity(cleanWord, language, category);
            wordJpaRepository.save(entity);

            processedWords.add(cleanWord);
            result.incrementLoaded();

        } catch (Exception e) {
            log.error("Error processing word '{}' at line '{}'", word, lineNumber, e);
            result.addError("Line " + lineNumber + ": Error processing '" + word + "': " + e.getMessage());
        }
    }

    private String normalizeWordForLanguage(String word, String language) {
        if (word == null) return  null;

        return switch (language) {
            case "ua" -> word.toLowerCase().trim(); // Cyrillic normalization
            case "de" -> word.toLowerCase().trim(); // German with umlauts
            case "fr" -> word.toLowerCase().trim(); // French with accents
            case "es" -> word.toLowerCase().trim(); // Spanish with accents
            default -> word.toLowerCase().trim(); // Default Latin
        };
    }

    private boolean isValidWordForLanguage(String word, String language) {
        if (word == null || word.trim().isEmpty()) return false;

        Pattern pattern = validationPatterns.get(language);
        if (pattern == null) {
            // Default validation for unsupported languages
            pattern = Pattern.compile("^[\\p{L}]{3,50}$"); // Unicode letters
        }

        return pattern.matcher(word).matches();
    }


    private boolean isBannedWord(String word, String language) {
        Set<String> languageBannedWords = bannedWords.get(language);
        return languageBannedWords != null && languageBannedWords.contains(word.toLowerCase());
    }

    /**
     * Creates a new {@link WordEntity} for persistence.
     * Sets the word value, category, and marks it as active.
     * The length and difficulty level will be automatically set by
     * the {@code @PrePersist} method in {@link WordEntity}.
     *
     * @param word the word value
     * @param category the category to assign
     * @return a new {@link WordEntity} ready for saving
     */
    private WordEntity createWordEntity(String word, String language, String category) {
        WordEntity entity = new WordEntity();
        entity.setValue(word);
        entity.setLanguage(language);
        entity.setCategory(category);
        entity.setIsActive(true);
        return entity;
    }

    /**
     * Reloads words from predefined files into the database.
     * The method performs the following steps:
     * 1. Deactivates all existing words in the database.
     * 2. Loads fresh words from default, programming, and animals word files.
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
        loadDefaultWords();
    }

    @Transactional
    public WordLoadResult loadWordsForLanguageFromContent(
            String content, String language, String category) {

        WordLoadResult result = new WordLoadResult(language, category);

        try {
            // validate language
            new Language(language);

            Set<String> processedWords = new HashSet<>();
            String[] lines = content.split("\n");

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                processWord(line, language, category, i + 1, processedWords, result);
            }

            log.info("Word loading from content completed for language {}: {} loaded, {} skipped",
                    language, result.getLoadedCount(), result.getSkippedCount());

        } catch (IllegalArgumentException e) {
            log.error("Invalid language: {}", language, e);
            result.addError("Invalid language: " + language);
        } catch (Exception e) {
            log.error("Error loading words from content for language {}", language, e);
            result.addError("Error processing content: " + e.getMessage());
        }

        return result;
    }

    public Map<String, Long> getLanguageStatistics () {
        Map<String, Long> stats = new HashMap<>();
        List<String> languages = wordJpaRepository.findSupportedLanguages();
        for (String lang: languages) {
            long count = wordJpaRepository.countByLanguageAndIsActiveTrue(lang);
            stats.put(lang, count);
        }


        return stats;
    }

    private void logLanguageStatistics() {
        Map<String, Long> stats = getLanguageStatistics();
        log.info("Language statistics:");
        stats.forEach((lang, count) -> {
            try {
                Language language = new Language(lang);
                log.info("  {} ({}): {} words", language.getDisplayName(), lang, count);
            } catch (Exception e) {
                log.info("  {}: {} words", lang, count);
            }
        });
    }

    /**
     * Represents the result of loading words from a file or source.
     * Tracks the number of successfully loaded words, skipped words,
     * and any errors encountered during processing.
     */
    public static class WordLoadResult {

        private final String language;
        private final String category;
        private int loadedCount = 0;
        private int skippedCount = 0;
        private final List<String> errors = new ArrayList<>();

        public WordLoadResult() {
            this(null, null);
        }

        public WordLoadResult(String language, String category) {
            this.language = language;
            this.category = category;
        }

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

        public String getLanguage () {
            return language;
        }

        public String getCategory () {
            return category;
        }
    }

}
