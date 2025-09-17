package com.join.tab.infra.repository.jpa.impl;

import com.join.tab.domain.model.Word;
import com.join.tab.domain.repository.WordRepository;
import com.join.tab.infra.entity.WordEntity;
import com.join.tab.infra.repository.jpa.WordJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Random;

@Repository
@Slf4j
public class JpaWordRepository implements WordRepository {

    private final WordJpaRepository jpaRepository;
    private final Random random = new Random();

    public JpaWordRepository(WordJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }


    @Override
    public Word getRandomWord(){
        Optional<WordEntity> randomWord = jpaRepository.findRandomWord();

        if (randomWord.isEmpty()) {
            log.warn("No words found in database, using fallback");
            throw new IllegalStateException("No words available in the database");
        }

        return convertToDomain(randomWord.get());
    }

    @Override
    public Word findById (Long id) {
        Optional<WordEntity> entity = jpaRepository.findById(id);
        return entity.map(this::convertToDomain).orElse(null);
    }

    public Word getRandomWordByDiffuculty(WordEntity.DiffucultyLevel diffuculty) {
        Optional<WordEntity> randomWord = jpaRepository.findRandomWordByDifficulty(diffuculty.name());
        return randomWord.map(this::convertToDomain).orElse(getRandomWord());
    }

    public boolean wordExists(String value) {
        return jpaRepository.findByValueIgnoreCase(value).isPresent();
    }

    public long getWordCount() {
        return jpaRepository.countByIsActiveTrue();
    }

    private Word convertToDomain(WordEntity entity) {
        return new Word(entity.getValue());
    }

    private WordEntity convertToEntity(Word domain) {
        WordEntity entity = new WordEntity();
        entity.setValue(domain.getValue());
        entity.setIsActive(true);

        return entity;
    }
}
