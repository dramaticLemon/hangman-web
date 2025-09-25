package com.join.tab.application.dto;

import com.join.tab.domain.aggregate.HangmanGame;
import com.join.tab.domain.enums.GameStatus;
import com.join.tab.domain.model.valueobject.Letter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Data Transfer Object (DTO) representing the state of a Hangman game.
 * Encapsulates information about the current state of the word, remaining tries,
 * game status, and (optionally) the full word when the game has finished.
 */
public class GameDto {
    private final static Logger log = LoggerFactory.getLogger(GameDto.class);

    private final String currentState;
    private final int remainingTries;
    private final GameStatus status;
    private final String word; // Only exposed when game is finished
    private final String language;
    private final String category;
    private final Set<Letter> guessedLetters;


    /**
     * Data Transfer Object (DTO) representing the state of a Hangman game.
     * Encapsulates information about the current state of the word, remaining tries,
     * game status, and (optionally) the full word when the game has finished.
     */
    public GameDto (String currentState, int remainingTries, GameStatus status,
                    String word, String language, String category,
                    Set<Letter> guessedLetters) {
        this.currentState = currentState;
        this.remainingTries = remainingTries;
        this.status = status;
        this.word = word;
        this.language = language;
        this.category = category;
        this.guessedLetters = guessedLetters;
    }

    /**
     * Creates a GameDto from a domain {@link HangmanGame} object.
     * The full word is only included if the game is finished.
     *
     * @param game the HangmanGame domain object
     * @return a GameDto representing the current game state
     */
    public static GameDto fromDomain(HangmanGame game) {
        log.info(game.getWord());
        String word = game.isInProgress() ? null : game.getWord();
        String category = game.getPreferences().hasCategory() ? game.getPreferences().getCategory() : null;

        return new GameDto(
                game.getCurrentState(),
                game.getRemainingTries(),
                game.getStatus(),
                word,
                game.getPreferences().getLanguage().getCode(),
                category,
                game.getGuessedLetters()
        );
    }

    public String getCurrentState() {
        return currentState;
    }

    public int getRemainingTries() {
        return remainingTries;
    }

    public GameStatus getStatus() {
        return status;
    }

    public String getWord() {
        return word;
    }

    public String getLanguage() {
        return language;
    }

    public String getCategory () {
        return category;
    }

    public Set<Letter> getGuessedLetters () {
        return guessedLetters;
    }
}
