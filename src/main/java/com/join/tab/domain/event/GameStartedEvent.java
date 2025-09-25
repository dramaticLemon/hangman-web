package com.join.tab.domain.event;

import com.join.tab.domain.model.valueobject.GameId;
import com.join.tab.domain.model.valueobject.Language;

/**
 * Domain event that represents the start of a new HangmanGame
 *
 * This event stores the {@link GameId} of the game and the word that
 * was selected for the game. It is triggered when a new game is created.
 *
 * Usage:
 * <pre>
 *     GameStartEvent event = new GameStartedEvent(
 *          new GameId("session123"), "example");
 *     String selectedWord = event.getWord();
 * </pre>
**/
public class GameStartedEvent extends GameEvent{

    private final String word;
    private final Language language;
    private final String category;

    /**
     * Creates a new GameStartedEvent
     * @param gameId the ID of the game that was started
     * @param word the word chosen for the game
     */
    public GameStartedEvent(GameId gameId, String word, Language language, String category) {
        super(gameId);

        this.word = word;
        this.language = language;
        this.category = category;

    }

    /**
     * Returns the word chosen for the game.
     *
     * @return the game word as a string
     */
    public String getWord() {
        return word;
    }

    public Language getLanguage() {
        return this.language;
    }

    public String getCategory() {
        return this.category;
    }
}
