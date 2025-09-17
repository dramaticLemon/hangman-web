package com.join.tab.domain.model;

import lombok.Getter;

import java.util.Objects;

// value object
@Getter
public class Letter {
    private final char value;

    public Letter(char value) {
        if (! Character.isLetter(value)) {
            throw new IllegalArgumentException("Must be a valid letter");
        }
        this.value = Character.toLowerCase(value);
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
