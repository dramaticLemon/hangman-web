package com.join.tab.application.dto;

import com.join.tab.domain.aggregate.HangmanGame;
import com.join.tab.domain.enums.GameStatus;

public class GameDto {
    private final String currentState;
    private final int remainingTries;
    private final GameStatus status;
    private final String word; // Only exposed when game is finished

    public GameDto (String currentState, int remainingTries, GameStatus status, String word) {
        this.currentState = currentState;
        this.remainingTries = remainingTries;
        this.status = status;
        this.word = word;
    }

    public static GameDto fromDomain (HangmanGame game) {
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
