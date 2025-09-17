package com.join.tab.domain.exception;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException (String message) {
        super(message);
    }
}
