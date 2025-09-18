package com.join.tab.domain.event;

import com.join.tab.domain.model.valueobject.GameId;
import lombok.Getter;

/**
 * Domain event that represent the end a Hangman game.
 *
 * This event stores the {@link GameId} of the finished game,
 * whether the player won, and the word that was used in the game.
 *
 * Usage:
 * <pre>
 *     GameEndedEvent event = new GameEndedEvent(new GameID("ex123"), true, "ex123");
 *     boolean didWin = event.isPlayerWon();
 *     String finalWord = event.getWord();
 * </pre>
 */
@Getter
public class GameEndedEvent extends  GameEvent{
    private final boolean playerWon;
    private final String word;

    public GameEndedEvent (GameId gameId, boolean playerWon, String word) {
        super(gameId);
        this.playerWon = playerWon;
        this.word = word;
    }

}
