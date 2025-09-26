package com.join.tab.domain.valueobject;


import java.util.Objects;

/**
 * Represents a unique identifier for a Hangman game.
 * This is a Value Object that encapsulates the game ID as a {@link String}.
 * Is ensures that the ID is not null or empty.
 * Features:
 * - Immutability: Once created, the value cannot be changed.
 * - Proper {@link #equals(Object)} and {@link #hashCode()} implementations
 *   for use in collections and comparisons.
 * Usage:
 * <pre>
 *     GameId gameId = new GameId("session134");
 * </pre>
 */
public class GameId {
    private final String value;

    /**
     * Create a new GameID
     *
     * @param value the string value of the game ID; must not be null or empty
     * @throws IllegalArgumentException if the value is null or empty.
     */
    public GameId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Game id cannot be null or empty");
        }
        this.value = value;
    }

    /**
     * Returns the string value of GameId
     *
     * @return the game ID as a string.
     */
    public String getValue() {
        return this.value;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameId gameId = (GameId) o;
        return Objects.equals(value, gameId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
