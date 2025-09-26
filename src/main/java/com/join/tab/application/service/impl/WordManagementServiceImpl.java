package com.join.tab.application.service.impl;

import com.join.tab.application.service.WordManagementService;
import com.join.tab.domain.valueobject.Language;
import com.join.tab.infra.entity.WordEntity;
import com.join.tab.infra.repository.jpa.WordJpaRepository;
import com.join.tab.infra.service.WordLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WordManagementServiceImpl implements WordManagementService {
    private static final Logger log = LoggerFactory.getLogger(WordManagementServiceImpl.class);

    private final WordJpaRepository wordJpaRepository;
    private final WordLoaderService wordLoadersService;

    public WordManagementServiceImpl(
            WordJpaRepository wordJpaRepository,
            WordLoaderService wordLoadService) {
        this.wordJpaRepository = wordJpaRepository;
        this.wordLoadersService = wordLoadService;
    }

    @Override
    public WordLoaderService.WordLoadResult loadWordsFromFile(String filePath, String category) {
        return loadWordsFromFileWithLanguage(filePath, "en", category);
    }

    public WordLoaderService.WordLoadResult loadWordsFromFileWithLanguage(String filePath, String language, String category) {
        try {
            return wordLoadersService.loadWordsFromFile(filePath, language, category);
        } catch (Exception e) {
            log.error("Failed to load words form file: {} for language: {}", filePath, language, e);
            WordLoaderService.WordLoadResult result = new WordLoaderService.WordLoadResult(language, category);
            result.addError("Failed to load words: " + e.getMessage());
            return result;
        }
    }

    @Override
    public WordLoaderService.WordLoadResult loadWordsFromStream(InputStream inputStream, String language, String category) {
        try {
            return wordLoadersService.loadWordsFromStream(inputStream, language, category);
        } catch (Exception e) {
            log.error("Failed to load words for language: {}", language);
            WordLoaderService.WordLoadResult result = new WordLoaderService.WordLoadResult(language, category);
            result.addError("Failed to load words: " + e.getMessage());
            return result;
        }
    }

    public WordLoaderService.WordLoadResult loadWordsFromContent(String content, String language, String category) {
        try {
            return wordLoadersService.loadWordsForLanguageFromContent(content, language, category);
        } catch (Exception e) {
            log.error("Failed to load words from content for language: {}", language, e);
            WordLoaderService.WordLoadResult result = new WordLoaderService.WordLoadResult(language, category);
            result.addError("Failed to load words from content: " + e.getMessage());
            return result;
        }
    }

    @Override
    public void reloadAllWords () {
        try {
            wordLoadersService.reloadWordFromFiles();
            log.info("Successfully reloaded all words");
        } catch (Exception e) {
            log.error("Failed to reload words", e);
            throw new RuntimeException("Failed to reload words: " + e.getMessage());
        }

    }

    @Override
    public long getWordCount () {
        return wordJpaRepository.countByIsActiveTrue();
    }

    public long getWordCountByLanguage(String language) {
        try {
            Language lang = new Language(language);
            return wordJpaRepository.countByLanguageAndIsActiveTrue(language);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid language code: {}", language);
            return 0;
        }
    }

    public Map<String, Long> getWordCountByAllLanguages() {
        return wordLoadersService.getLanguageStatistics();
    }

    @Override
    public List<String> getAvailableCategories () {
        return getAvailableCategoriesForLanguage("en") ;
    }

    public List<String> getAvailableCategoriesForLanguage(String language) {
        try {
            Language lang  = new Language(language);
            return wordJpaRepository.findCategoriesByLanguage(language);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid language code: {}, returning empty categories", language);
            return List.of();
        }
    }

    @Override
    public boolean addWord(String word, String language, String category) {
        return addWordWithLanguage(word, language, category);
    }

    public boolean addWordWithLanguage(String word, String language, String category) {
        try {
            new Language(language); // Validate language

            if (!isValidWordForLanguage(word, language)) {
                log.warn("Attempted to add invalid word for language {}: {}", language, word);
                return false;
            }

            String cleanWord = word.toLowerCase().trim();

            if (wordJpaRepository.findByValueIgnoreCaseAndLanguage(cleanWord, language).isPresent()) {
                log.info("Word already exists for language {}: {}", language, cleanWord);
                return false;
            }

            WordEntity entity = new WordEntity();
            entity.setValue(cleanWord);
            entity.setLanguage(language);
            entity.setCategory(category);
            entity.setIsActive(true);

            wordJpaRepository.save(entity);
            log.info("Successfully added word: {} in language: {} and category: {}", cleanWord, language, category);
            return true;

        } catch (IllegalArgumentException e) {
            log.error("Invalid language or word: {} for language: {}", word, language, e);
            return false;
        } catch (Exception e) {
            log.error("Failed to add word: {} for language: {}", word, language, e);
            return false;
        }
    }

    @Override
    public boolean removeWord(String word, String language) {
        return removeWordForLanguage(word, language);
    }

    public boolean removeWordForLanguage(String word, String language) {
        try {
            Optional<WordEntity> entity = wordJpaRepository.findByValueIgnoreCaseAndLanguage(word.trim(), language);

            if (entity.isPresent()) {
                entity.get().setIsActive(false);
                wordJpaRepository.save(entity.get());
                log.info("Successfully deactivated word: {} for language: {}", word, language);
                return true;
            }

            log.warn("Word not found for removal in language {}: {}", language, word);
            return false;

        } catch (Exception e) {
            log.error("Failed to remove word: {} for language: {}", word, language, e);
            return false;
        }
    }

    @Override
    public boolean wordExists(String word, String language) {
        return wordExistsForLanguage(word, language); // Default to English
    }

    public boolean wordExistsForLanguage(String word, String language) {
        return wordJpaRepository.findByValueIgnoreCaseAndLanguage(word.trim(), language).isPresent();
    }

    private boolean isValidWordForLanguage(String word, String language) {
        if (word == null || word.trim().isEmpty()) return false;

        String cleanWord = word.trim();
        return switch (language) {
            case "ua" -> cleanWord.matches("^[А-ЯІЇЄҐа-яіїєґ]{3,50}$");
            case "de" -> cleanWord.matches("^[a-zA-ZäöüÄÖÜß]{3,50}$");
            case "fr" -> cleanWord.matches("^[a-zA-ZàâäéèêëïîôöùûüÿçÀÂÄÉÈÊËÏÎÔÖÙÛÜŸÇ]{3,50}$");
            case "es" -> cleanWord.matches("^[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ]{3,50}$");
            case "en" -> cleanWord.matches("^[a-zA-Z]{3,50}$");
            default -> cleanWord.matches("^[\\p{L}]{3,50}$"); // Unicode letters
        };
    }
}
