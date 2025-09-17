package com.join.tab.domain.event;

import com.join.tab.domain.model.GameId;

public class GameStartedEvent extends GameEvent{

    private final String word;

    public GameStartedEvent(GameId gameId, String word) {
        super(gameId);
        this.word = word;
    }

    public String gameWord() {
        return word;
    }
}
