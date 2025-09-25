package com.join.tab.domain.event;

import com.join.tab.domain.valueobject.GameId;
import lombok.Getter;

/**
 * Base class for all domain events related to a Hangman game.
 *
 * This class captures common information for a game-related events.
 * such as the {@link GameId} of the game and the timestamp when
 * the event occurred.
 *
 * Features:
 * - Immutable fields for game ID and event timestamp.
 * - Intended to be extended by specific event typed (e.g., letter guessed, game started)
 *
 * Usage:
 * <pre>
 *     public class GameStartEvent extends GameEvent {
 *         public GameStartEvent(GameId gameId) {
 *             super(gameId);
 *         }
 *     }
 * </pre>
 */
@Getter
public abstract class GameEvent {
    private final GameId gameid;
    private final long timestamp;

    protected GameEvent(GameId gameId) {
        this.gameid = gameId;
        this.timestamp = System.currentTimeMillis();
    }

}
