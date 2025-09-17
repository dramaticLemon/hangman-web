package com.join.tab.domain.model;

import lombok.Getter;

import java.util.Objects;

// domain models
@Getter
public class Word {
    private final String value;

    public Word(String value) throws IllegalAccessException {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalAccessException("Word cannot be null or empty");
        }
        this.value = value;
    }

    public int getLength() {
        return value.length();
    }

    public char getCharAt(int index) {
        return value.charAt(index);
    }

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
}
