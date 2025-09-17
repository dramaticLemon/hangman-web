package com.join.tab.domain.event;

import com.join.tab.domain.model.GameId;
import lombok.Getter;

@Getter
public abstract class GameEvent {
    private final GameId gameid;
    private final long timestamp;

    protected GameEvent(GameId gameId) {
        this.gameid = gameId;
        this.timestamp = System.currentTimeMillis();
    }

}
