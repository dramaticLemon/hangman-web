package com.join.tab.domain.exception;


public class LetterAlreadyGuessedException extends  RuntimeException{
    private final char letter;

    public LetterAlreadyGuessedException(char letter) {
        super("Letter '" + letter + "' had already been guessed");
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }
}
