package com.join.tab.application.dto;

import com.join.tab.domain.aggregate.HangmanGame;
import com.join.tab.domain.enums.GameStatus;


/**
 * Data Transfer Object (DTO) representing the result of a single guess in a Hangman game.
 * Encapsulates the updated state of the word, remaining tries, game status,
 * the full word (if the game is finished), whether the guess was correct,
 * and the guessed letter itself.
 */
public class GuessDto {
    private final String currentState;
    private final int remainingTries;
    private final GameStatus status;
    private final String word;
    private final String language;
    private final boolean wasCorrect;
    private final char guessedLetter;

    /**
     * Constructs a new GuessDto.
     *
     * @param currentState   the current masked state of the word
     * @param remainingTries number of remaining incorrect guesses allowed
     * @param status         the current {@link GameStatus}
     * @param word           the full word, exposed only if the game has ended
     * @param wasCorrect     whether the last guessed letter was correct
     * @param guessedLetter  the letter that was guessed
     */
    public GuessDto (String currentState, int remainingTries, GameStatus status,
                     String word, String language, boolean wasCorrect, char guessedLetter
    ) {
        this.currentState = currentState;
        this.remainingTries = remainingTries;
        this.status = status;
        this.word = word;
        this.language = language;
        this.wasCorrect = wasCorrect;
        this.guessedLetter = guessedLetter;
    }

    public static GuessDto fromDomain (
            HangmanGame game,
            HangmanGame.GuessResult result) {
        String word = game.isInProgress() ? null : game.getWord();
        return new GuessDto(
                result.getCurrentState(),
                result.getRemainingTries(),
                result.getGameStatus(),
                word,
                result.getLanguage().getCode(),
                result.isWasCorrect(),
                ' '
        );
    }

    public String getCurrentState () {
        return this.currentState;
    }

    public int getRemainingTries () {
        return this.remainingTries;
    }

    public GameStatus getStatus () {
        return this.status;
    }

    public String getWord () {
        return this.word;
    }

    public boolean isWasCorrect () {
        return this.wasCorrect;
    }

    public char getGuessedLetter () {
        return this.guessedLetter;
    }

    public String getLanguage () {
        return language;
    }
}