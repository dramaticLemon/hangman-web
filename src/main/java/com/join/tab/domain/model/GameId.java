package com.join.tab.domain.model;

import lombok.Getter;

import java.util.Objects;

// value object
@Getter
public class GameId {
    private final String value;

    public GameId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Game id cannot be null or empty");
        }
        this.value = value;
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
