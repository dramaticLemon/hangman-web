package com.join.tab.domain.event;

import com.join.tab.domain.model.GameId;
import lombok.Getter;

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
