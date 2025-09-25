package com.join.tab.domain.valueobject;

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
        if (!isValidLetter(value)) {
            throw new IllegalArgumentException("Must be a valid letter");
        }
        this.value = Character.toLowerCase(value);
    }

    private boolean isValidLetter(char letter) {
        return isLatinLetter(letter) || isCyrillicLetter(letter);
    }

    private boolean isLatinLetter(char letter) {
        return (letter >= 'a' && letter <= 'z') || (letter >= 'A' && letter <= 'Z');
    }

    private boolean isCyrillicLetter(char letter) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(letter);
        return block == Character.UnicodeBlock.CYRILLIC
                || block == Character.UnicodeBlock.CYRILLIC_SUPPLEMENTARY
                || block == Character.UnicodeBlock.CYRILLIC_EXTENDED_A
                || block == Character.UnicodeBlock.CYRILLIC_EXTENDED_B;
    }

    public boolean isLatinAlphabet() {
        return isLatinLetter(value);
    }

    public boolean isCyrillicAlphabet() {
        return isCyrillicLetter(value);
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

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
