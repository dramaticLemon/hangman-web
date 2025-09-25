package com.join.tab.domain.exception;

public class UnsupportedLanguageException extends RuntimeException {
    private final String language;


    public UnsupportedLanguageException (String language) {
        super("Unsupported language: " + language);
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

}
