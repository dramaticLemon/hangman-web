package com.join.tab.infra.repository.memory;

import com.join.tab.domain.model.Word;
import com.join.tab.domain.model.valueobject.GamePreferences;
import com.join.tab.domain.model.valueobject.Language;
import com.join.tab.domain.repository.WordRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * In-memory implementation of {@link WordRepository}
 *
 * Provides a fixed list of words for testing or demonstration purposes.
 * Does not persist data and return random words or words by index.
 */
@Repository
public class InMemoryWordRepository implements WordRepository {

    private final List<String> words = Arrays.asList(
            "java", "spring", "hibernate", "database", "computer",
            "programming", "software", "algorithm", "framework", "architecture"
    );

    private final Random random = new Random();

    /**
     * Returns a random word from the in-memory list.
     * @return a {@link Word} selected randomly
     */
    @Override
    public Word getRandomWord() {
        String randomWord = words.get(random.nextInt(words.size()));
        return new Word(randomWord, Language.defaultLanguage());
    }

    @Override
    public Word getRandomWordByPreferences (GamePreferences preferences) {
        return null;
    }

    /**
     * Returns a word its "ID", treated as an index in the list.
     * Falls back to a random word if the index is valid.
     * @param id the ID of the word to find
     * @return a {@link Word} corresponding to the index or a random word
     */
    @Override
    public Word findById(Long id) {
        // For demo purposes, just return a word by index
        if (id >= 0 && id < words.size()) {
            return new Word(words.get(id.intValue()), Language.defaultLanguage());
        }
        return getRandomWord();
    }
}