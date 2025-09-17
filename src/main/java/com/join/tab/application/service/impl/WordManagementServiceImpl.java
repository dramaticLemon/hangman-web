package com.join.tab.application.service.impl;

import com.join.tab.application.service.WordManagementService;
import com.join.tab.infra.entity.WordEntity;
import com.join.tab.infra.repository.jpa.WordJpaRepository;
import com.join.tab.infra.service.WordLoadersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class WordManagementServiceImpl implements WordManagementService {
    private static final Logger log = LoggerFactory.getLogger(WordManagementServiceImpl.class);

    private final WordJpaRepository wordJpaRepository;
    private final WordLoadersService wordLoadersService;
    private final Pattern validWordPattern = Pattern.compile("^[a-zA-Z]{3,50}$");

    public WordManagementServiceImpl(WordJpaRepository wordJpaRepository,
                                    WordLoadersService wordLoadService) {
        this.wordJpaRepository = wordJpaRepository;
        this.wordLoadersService = wordLoadService;
    }
    @Override
    public WordLoadersService.WordLoadResult loadWordsFromFile (String filePath, String category) {
        try {
            return wordLoadersService.loadWordsFromFile(filePath, category);
        } catch (Exception e) {
            log.error("Failed to load words form file: {}", filePath, e);
            WordLoadersService.WordLoadResult result = new WordLoadersService.WordLoadResult();
            result.addError("Failed to load words: " + e.getMessage());
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

    @Override
    public List<String> getAvailableCategories () {
        return wordJpaRepository.findAllCategories();
    }

    @Override
    public boolean addWord (String word, String category) {
        try {
            if (!isValidWord(word)) {
                log.warn("Attempted to add invalid word: {}", word);
                return false;
            }

            String cleanWord = word.toLowerCase().trim();

            if (wordJpaRepository.findByValueIgnoreCase(cleanWord).isPresent()) {
                log.info("Word already exists: {}", cleanWord);
                return false;
            }

            WordEntity entity = new WordEntity();
            entity.setValue(cleanWord);
            entity.setCategory(category);
            entity.setIsActive(true);

            wordJpaRepository.save(entity);
            log.info("Successfully added word: {} in category: {}", cleanWord, category);
            return true;

        } catch (Exception e) {
            log.error("Failed to add word: {}", word, e);
            return false;
        }
    }

    @Override
    public boolean removeWord (String word) {
        try {
            Optional<WordEntity> entity = wordJpaRepository.findByValueIgnoreCase(word.trim());

            if (entity.isPresent()) {
                entity.get().setIsActive(false);
                wordJpaRepository.save(entity.get());
                log.info("Successfully deactivated word: {}", word);
                return true;
            }

            log.warn("Word not found for removal: {}", word);
            return false;
        } catch (Exception e) {
            log.error("Failed to remove word: {}", word, e);
            return false;
        }
    }

    @Override
    public boolean wordExists (String word) {
        return wordJpaRepository.findByValueIgnoreCase(word.trim()).isPresent();
    }

    private boolean isValidWord(String word) {
        return word != null
                && validWordPattern.matcher(word.trim()).matches()
                && word.trim().length() >= 3
                && word.trim().length() <= 50;
    }
}
