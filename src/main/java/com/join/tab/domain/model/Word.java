package com.join.tab.domain.model;

import com.join.tab.domain.valueobject.Language;

import java.util.Objects;

/**
 * Represents a word used in the Hangman game.
 *
 * This is a domain model that encapsulates the string value of a word.
 * It provides utility methods to access the word's characters and check
 * if a letter exists within the word.
 *
 * Features:
 *  - Immutability: The word cannot be changed once created.
 *  - Provides character-level access and length information
 *  - Proper {@link #equals(Object)} and {@link #hashCode()} implementations
 *    for use in collection and comparisons.
 *
 * Usage:
 * <pre>
 *     Word word = new Word("test");
 *     boolean hasE = word.contains('e'); // true
 * </pre>
 */
public class Word {
    private final String value;
    private final Language language;

    /**
     * Create a new Word.
     *
     * @param value the string value of the word; must not be null or empty
     * @throws IllegalArgumentException if the values is null or empty
     */
    public Word(String value, Language language) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Word cannot be null or empty");
        }
        if (language == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }

        this.value = value;
        this.language = language;
    }

    private void validateWordForLanguage() {
        if (!isValidForLanguage(value, language)) {
            throw  new IllegalArgumentException(
                    String.format("Word '%s' is not valid for language '%s' ", value, language.getCode()));
        }
    }

    private boolean isValidForLanguage(String word, Language lang) {
        return switch (lang.getCode()) {
            case "ua" -> word.matches("^[а-щьюяґєіїА-ЩЬЮЯҐЄІЇ]+$");
            case "en", "de", "fr", "es" -> word.matches("^[a-z]+$");
            default -> word.matches("^[a-zA-Zа-яёА-ЯЁґєіїҐЄІЇ]+$"); // mixed
        };
    }
    /**
     * Returns the string value of this Word.
     *
     * @return the word as a string
     */
    public String getValue() {
        return this.value;
    }

    public Language getLanguage() {
        return this.language;
    }

    /**
     * Returns the length of the word.
     *
     * @return the number of characters if the word
     */
    public int getLength() {
        return value.length();
    }

    /**
     * Returns the character at the specified index in the word.
     *
     * @param index the index of the character to return.
     * @return the character at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public char getCharAt(int index) {
        return value.charAt(index);
    }

    /**
     * Check if the word contains the given letter.
     *
     * @param letter the letter to check
     * @return {@code true} if the letter exists in the word; {@code false} otherwise
     */
    public boolean contains(char letter) {
        return value.indexOf(Character.toLowerCase(letter)) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return Objects.equals(value, word.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.format("Word{value='%s', language='%s'}", value, language.getCode());
    }
}
