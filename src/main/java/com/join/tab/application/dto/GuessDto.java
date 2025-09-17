package com.join.tab.application.dto;

import com.join.tab.domain.aggregate.HangmanGame;
import com.join.tab.domain.enums.GameStatus;

public class GuessDto {
    private final String currentState;
    private final int remainingTries;
    private final GameStatus status;
    private final String word;
    private final boolean wasCorrect;
    private final char guessedLetter;

    public GuessDto(String currentState, int remainingTries, GameStatus status,
                    String word, boolean wasCorrect, char guessedLetter) {
        this.currentState = currentState;
        this.remainingTries = remainingTries;
        this.status = status;
        this.word = word;
        this.wasCorrect = wasCorrect;
        this.guessedLetter = guessedLetter;
    }

    public static GuessDto fromDomain(HangmanGame game, HangmanGame.GuessResult result) {
        String word = game.isInProgress() ? null : game.getWord();
        return new GuessDto(
                result.getCurrentState(),
                result.getRemainingTries(),
                result.getGameStatus(),
                word,
                result.isWasCorrect(),
                ' ' // You'd need to pass the guessed letter here
        );
    }

    public String getCurrentState() {
        return this.currentState;
    }

    public int getRemainingTries() {
        return this.remainingTries;
    }

    public GameStatus getStatus() {
        return this.status;
    }

    public String getWord() {
        return this.word;
    }

    public boolean isWasCorrect() {
        return this.wasCorrect;
    }

    public char getGuessedLetter() {
        return this.guessedLetter;
    }

}