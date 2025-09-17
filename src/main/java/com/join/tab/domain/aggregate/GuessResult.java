package com.join.tab.domain.aggregate;

import com.join.tab.domain.enums.GameStatus;

public class GuessResult {

    private final String currentState;
    private final int remainingTries;
    private final GameStatus gameStatus;
    private final boolean wasCorrect;
    private final boolean wasAlreadyGuessed;

    public GuessResult(String currentState, int remainingTries, GameStatus gameStatus,
                       boolean wasCorrect, boolean wasAlreadyGuessed) {
        this.currentState = currentState;
        this.remainingTries = remainingTries;
        this.gameStatus = gameStatus;
        this.wasCorrect = wasCorrect;
        this.wasAlreadyGuessed = wasAlreadyGuessed;
    }

    public String getCurrentState() { return currentState; }
    public int getRemainingTries() { return remainingTries; }
    public GameStatus getGameStatus() { return gameStatus; }
    public boolean wasCorrect() { return wasCorrect; }
    public boolean wasAlreadyGuessed() { return wasAlreadyGuessed; }

}
