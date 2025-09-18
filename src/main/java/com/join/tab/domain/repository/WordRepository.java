package com.join.tab.domain.repository;

import com.join.tab.domain.model.Word;

/**
 * Repository interface for accessing {@link Word} entities.
 *
 * Provides methods to fetch random words or find a word by its ID.
 */
public interface WordRepository {

    /**
     * Returns a random word from the repository
     *
     * @return a randomly selected {@link Word}
     */
    Word getRandomWord();

    /**
     * Finds a word by its unique ID.
     *
     * @param id the ID of the word to find
     * @return the {@link Word} with the given ID, or {@code null} if not found
     */
    Word findById(Long id);

}
