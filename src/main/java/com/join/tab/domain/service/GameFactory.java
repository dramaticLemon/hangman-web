package com.join.tab.domain.service;

import com.join.tab.domain.aggregate.HangmanGame;
import com.join.tab.domain.valueobject.GameId;
import com.join.tab.domain.model.Word;
import com.join.tab.domain.valueobject.GamePreferences;
import com.join.tab.domain.valueobject.Language;
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
     * Creates a new game with a random selected default language word.
     *
     * @param gameId the unique ID for the new game
     * @return a new {@link HangmanGame} instance
     */
    public HangmanGame createNewGame(GameId gameId) {
        return createNewGameWithPreferences(gameId, GamePreferences.defaultPreferences());
    }

    public HangmanGame createNewGameWithLanguage(GameId gameId, Language language) {
        GamePreferences preferences = GamePreferences.withLanguage(language);
        return createNewGameWithPreferences(gameId, preferences);
    }

    public HangmanGame createNewGameWithPreferences(GameId gamId, GamePreferences preferences) {
        Word randomWord = wordRepository.getRandomWordByPreferences(preferences);
        return new HangmanGame(gamId, randomWord, preferences);
    }

    /**
     * Create a new game with specific word value
     *
     * @param gameId the unique ID for the new game
     * @param wordValue the word to use in the game
     * @return a new {@link HangmanGame} instance
     */
    public HangmanGame createGameWithWord(GameId gameId, String wordValue, Language language){
        Word word = new Word(wordValue, language);
        GamePreferences preferences = GamePreferences.withLanguage(language);
        return new HangmanGame(gameId, word, preferences);
    }
}
