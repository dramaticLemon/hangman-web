package com.join.tab.domain.service;

import com.join.tab.domain.aggregate.HangmanGame;
import com.join.tab.domain.model.valueobject.GameId;
import com.join.tab.domain.model.Word;
import com.join.tab.domain.repository.WordRepository;

/**
 * Factory class for creating new {@link HangmanGame} instances.
 *
 * Provides methods to start a game with either a random word
 * form the {@link WordRepository} or a specific word value.
 */
public class GameFactory {
    private final WordRepository wordRepository;

    /**
     * Create a GameFactory with the given word repository
     *
     * @param wordRepository the repository used to fetch words.
     */
    public GameFactory(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    /**
     * Creates a new game with a random selected word.
     *
     * @param gameId the unique ID for the new game
     * @return a new {@link HangmanGame} instance
     */
    public HangmanGame createNewGame(GameId gameId) {
        Word randomWord = wordRepository.getRandomWord();
        return new HangmanGame(gameId, randomWord);
    }

    /**
     * Create a new game with specific word value
     *
     * @param gameId the unique ID for the new game
     * @param wordValue the word to use in the game
     * @return a new {@link HangmanGame} instance
     */
    public HangmanGame createGameWithWord(GameId gameId, String wordValue){
        Word word = new Word(wordValue);
        return new HangmanGame(gameId, word);
    }
}
