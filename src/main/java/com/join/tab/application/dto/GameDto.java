package com.join.tab.application.dto;

import com.join.tab.domain.aggregate.HangmanGame;
import com.join.tab.domain.enums.GameStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Transfer Object (DTO) representing the state of a Hangman game.
 *
 * Encapsulates information about the current state of the word, remaining tries,
 * game status, and (optionally) the full word when the game has finished.
 */
public class GameDto {
    private final static Logger log = LoggerFactory.getLogger(GameDto.class);

    private final String currentState;
    private final int remainingTries;
    private final GameStatus status;
    private final String word; // Only exposed when game is finished

    /**
     * Data Transfer Object (DTO) representing the state of a Hangman game.
     *
     * Encapsulates information about the current state of the word, remaining tries,
     * game status, and (optionally) the full word when the game has finished.
     */
    public GameDto (String currentState, int remainingTries, GameStatus status, String word) {
        this.currentState = currentState;
        this.remainingTries = remainingTries;
        this.status = status;
        this.word = word;
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
        return new GameDto(
                game.getCurrentState(),
                game.getRemainingTries(),
                game.getStatus(),
                word
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
}
