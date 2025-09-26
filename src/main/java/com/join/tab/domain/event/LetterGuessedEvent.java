package com.join.tab.domain.event;

import com.join.tab.domain.valueobject.GameId;
import com.join.tab.domain.valueobject.Letter;

/**
 * Domain event that represents a letter guess made in a Hangman game.
 *
 * This event stores the {@link GameId} of the game, the {@link Letter} that
 * was guessed, and whether the guess was correct.
 *
 * Usage:
 * <pre>
 *     LetterGuessedEvent event = new LetterGuessedEvent(new GameId("session123"), new Letter('a'), true);
 *     boolean correct = event.isWasCorrect();
 * </pre>
 */
public class LetterGuessedEvent extends  GameEvent{
    private final Letter letter;
    private final boolean wasCorrect;

    /**
     * Created a new LetterGuessedEvent.
     *
     * @param gameId the ID of the game where the guess was made
     * @param letter the letter that was guessed
     * @param wasCorrect {@code true} if the guess was correct, {@code false} otherwise
     */
    public LetterGuessedEvent(GameId gameId, Letter letter, boolean wasCorrect) {
        super(gameId);
        this.letter = letter;
        this.wasCorrect = wasCorrect;
    }

    public Letter getLetter () {
        return letter;
    }

    public boolean isWasCorrect () {
        return wasCorrect;
    }
}
