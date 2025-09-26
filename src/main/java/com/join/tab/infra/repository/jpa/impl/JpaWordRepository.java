package com.join.tab.infra.repository.jpa.impl;

import com.join.tab.domain.enums.DifficultyLevel;
import com.join.tab.domain.model.Word;
import com.join.tab.domain.valueobject.GamePreferences;
import com.join.tab.domain.valueobject.Language;
import com.join.tab.domain.repository.WordRepository;
import com.join.tab.infra.entity.WordEntity;
import com.join.tab.infra.repository.jpa.WordJpaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * JPA-based implementation of the {@link WordRepository} interface
 * Provides methods to fetch words from the database and convert them
 * between the JPA entity ({@link WordEntity}) and the domain model ({@link Word}).
 * Features:
 * - Retrieves random words, by ID, or by difficulty level.
 * - Check if a word exists and counts active words.
 * - Convert between domain and entity representations.
 * - Logs warnings if the database contains no words.
 */
@Repository
@Primary
public class JpaWordRepository implements WordRepository {

    private static final Logger log = LoggerFactory.getLogger(JpaWordRepository.class);
    private final WordJpaRepository jpaRepository;
    private final Random random = new Random();

    /**
     * Constructor a new JpaWordRepository using the underlying Jpa repository.
     *
     * @param jpaRepository the Jpa repository for WordEntity
     */
    public JpaWordRepository(WordJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }


    /**
     * Return a random active word form the database.
     *
     * @return a {@link Word} form the database
     * @throws IllegalStateException if not words are available
     */
    @Override
    public Word getRandomWord(){
        return getRandomWordByPreferences(GamePreferences.defaultPreferences());
    }

    public Word getRandomWordByPreferences(GamePreferences preferences) {
        String categoryParam = preferences.hasCategory() ? preferences.getCategory() : null;
        String difficultyParam = preferences.hasDifficulty() ? preferences.getDifficulty().name() : null;

        Optional<WordEntity> randomWord = jpaRepository.findRandomWordByCriteria(
                preferences.getLanguage().getCode(),
                categoryParam,
                difficultyParam
        );

        if (randomWord.isEmpty()) {
            log.warn("No words found for preferences: {}, trying language only", preferences);
            return getRandomWordByLanguage(preferences.getLanguage());
        }


        return convertToDomain(randomWord.get());
    }

    public Word getRandomWordByLanguageAndCategory(Language language, String category) {
        Optional<WordEntity> randomWord = jpaRepository.findRandomWordByLanguageAndCategory(language.getCode(), category);

        if (randomWord.isEmpty()) {
            log.warn("No words found for language: {} and category: {}, trying language only",
                    language.getCode(), category);
            return getRandomWordByLanguage(language);
        }

        return convertToDomain(randomWord.get());
    }

    public Word getRandomWordByLanguage(Language language) {
        Optional<WordEntity> randomWord = jpaRepository.findRandomWordByLanguage(language.getCode());

        if (randomWord.isEmpty()) {
            log.warn("No words found for language: {}, trying fallback", language.getCode());
            return getFallbackWord();
        }

        return convertToDomain(randomWord.get());
    }

    /**
     * Find a word by its ID.
     *
     * @param id the ID of the word to find
     * @return the corresponding ({@link Word}), or ({@code null}) if not found
     */
    @Override
    public Word findById (Long id) {
        Optional<WordEntity> entity = jpaRepository.findById(id);
        return entity.map(this::convertToDomain).orElse(null);
    }

    /**
     * Returns a random word of a specific difficulty level.
     * Falls back a completely random word if none are found at the specified level.
     *
     * @param difficulty the difficulty level
     * @return a {@link Word} form the database
     */
    public Word getRandomWordByDifficulty( DifficultyLevel difficulty) {
        Optional<WordEntity> randomWord = jpaRepository.findRandomWordByDifficulty(difficulty.name());
        return randomWord.map(this::convertToDomain).orElse(getRandomWord());
    }

    /**
     * Check whether a word exists in the database.
     *
     * @param value the word value
     * @return {@code true } if the word exists, {@code false} otherwise
     */
    public boolean wordExists(String value) {
        return jpaRepository.findByValueIgnoreCase(value).isPresent();
    }

    /**
     * Returns the total count of active words in the database.
     *
     * @return the number of active words.
     */
    public long getWordCount() {
        return jpaRepository.countByIsActiveTrue();
    }

    public boolean wordExistsForLanguage(String value, Language language) {
        return jpaRepository.findByValueIgnoreCaseAndLanguage(value, language.getCode()).isPresent();
    }
    public List<String> getSupportedLanguages() {
        return jpaRepository.findSupportedLanguages();
    }

    public long getCategoriesByLanguage(Language language) {
        return jpaRepository.countByLanguageAndIsActiveTrue(language.getCode());
    }

    public long getWordCountByLanguage(Language language) {
        return jpaRepository.countByLanguageAndIsActiveTrue(language.getCode());
    }
    private Word getFallbackWord() {
        Optional<WordEntity> fallback = jpaRepository.findAnyRandomWord();
        if (fallback.isEmpty()) {
            log.error("No words available in database at all!");
            throw new IllegalStateException("No words available in the database");
        }

        log.info("Using fallback word: {} in language: {}",
                fallback.get().getValue(), fallback.get().getLanguage());
        return convertToDomain(fallback.get());
    }

    /**
     * Convert a {@link WordEntity} to the domain {@link Word} object.
     *
     * @param entity the Jpa entity
     * @return the corresponding domain object
     */
    private Word convertToDomain(WordEntity entity) {
        Language language = new Language(entity.getLanguage());
        return new Word(entity.getValue(), language);
    }

    /**
     * Converts a domain {@link Word} to the entity {@link WordEntity} object.
     *
     * @param domain the domain word
     * @return the corresponding entity object
     */
    private WordEntity convertToEntity(Word domain, String category) {
        WordEntity entity = new WordEntity();
        entity.setValue(domain.getValue());
        entity.setLanguage(domain.getLanguage().getCode());
        entity.setCategory(category);
        entity.setIsActive(true);

        return entity;
    }
}
