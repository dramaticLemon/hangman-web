package com.join.tab.domain.model.valueobject;

import java.util.Objects;

/**
 * Represents a single letter used in the Hangman game.
 *
 * This is a Value Object that encapsulates a single character.
 * It ensures that only valid letters (a-z, A-Z) are allowed and stores
 * the value in lowercase for consistency.
 *
 * Features:
 *  - Immutability: Once create, the value cannot be changed.
 *  - Proper {@link #equals(Object)} and {@link #hashCode()} implementations
 *    for use in collections and comparisons.
 *
 * Usage:
 * <pre>
 *     Letter letter = new Letter('A'); // stored as 'a'
 * </pre>
 */
public class Letter {
    private final char value;

    /**
     * Create a new Letter value object.
     *
     * @param value the character to represent; must be a valid letter(a-z or A-Z)
     * @throws IllegalArgumentException if the character is not a valid letter
     */
    public Letter(char value) {
        if (! Character.isLetter(value)) {
            throw new IllegalArgumentException("Must be a valid letter");
        }
        this.value = Character.toLowerCase(value);
    }

    /**
     * Returns the character value of this Letter
     *
     * @return the lowercase represented by this Letter
     */
    public char getValue() {
        return this.value;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Letter letter = (Letter) o;
        return value == letter.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
