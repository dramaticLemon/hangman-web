package com.join.tab.infra.repository.jpa.impl;

import com.join.tab.domain.model.Word;
import com.join.tab.domain.repository.WordRepository;
import com.join.tab.infra.entity.WordEntity;
import com.join.tab.infra.repository.jpa.WordJpaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Random;

/**
 * JPA-based implementation of the {@link WordRepository} interface
 *
 * Provides methods to fetch words from the database and convert them
 * between the JPA entity ({@link WordEntity}) and the domain model ({@link Word}).
 *
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
        Optional<WordEntity> randomWord = jpaRepository.findRandomWord();

        if (randomWord.isEmpty()) {
            log.warn("No words found in database, using fallback");
            throw new IllegalStateException("No words available in the database");
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
    public Word getRandomWordByDiffuculty(WordEntity.DifficultyLevel difficulty) {
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

    /**
     * Convert a {@link WordEntity} to the domain {@link Word} object.
     *
     * @param entity the Jpa entity
     * @return the corresponding domain object
     */
    private Word convertToDomain(WordEntity entity) {
        return new Word(entity.getValue());
    }

    /**
     * Converts a domain {@link Word} to the entity {@link WordEntity} object.
     *
     * @param domain the domain word
     * @return the corresponding entity object
     */
    private WordEntity convertToEntity(Word domain) {
        WordEntity entity = new WordEntity();
        entity.setValue(domain.getValue());
        entity.setIsActive(true);

        return entity;
    }
}
