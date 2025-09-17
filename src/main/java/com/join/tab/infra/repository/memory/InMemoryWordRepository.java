package com.join.tab.infra.repository.memory;

import com.join.tab.domain.model.Word;
import com.join.tab.domain.repository.WordRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Repository
public class InMemoryWordRepository implements WordRepository {

    private final List<String> words = Arrays.asList(
            "java", "spring", "hibernate", "database", "computer",
            "programming", "software", "algorithm", "framework", "architecture"
    );

    private final Random random = new Random();

    @Override
    public Word getRandomWord() {
        String randomWord = words.get(random.nextInt(words.size()));
        return new Word(randomWord);
    }

    @Override
    public Word findById(Long id) {
        // For demo purposes, just return a word by index
        if (id >= 0 && id < words.size()) {
            return new Word(words.get(id.intValue()));
        }
        return getRandomWord();
    }
}