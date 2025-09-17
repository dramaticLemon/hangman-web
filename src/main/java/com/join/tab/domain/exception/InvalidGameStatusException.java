package com.join.tab.domain.exception;

public class InvalidGameStatusException extends RuntimeException {
    public InvalidGameStatusException (String message) {
        super(message);
    }
}
