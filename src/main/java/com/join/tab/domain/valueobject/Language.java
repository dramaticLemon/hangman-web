package com.join.tab.domain.valueobject;

import java.util.Objects;
import java.util.Set;

public class Language {
    private static final Set<String> SUPPORTED_LANGUAGE = Set.of("en", "ua", "de", "fr", "es");
    private static final String DEFAULT_LANGUAGE = "en";

    private final String code;

    public Language(String code) {
        if (code == null || code.trim().isEmpty()) {
            this.code = DEFAULT_LANGUAGE;
        } else {
            String noramalizedCode = code.toLowerCase().trim();
            if (!SUPPORTED_LANGUAGE.contains(noramalizedCode)) {
                throw new IllegalArgumentException("Unsupported language: " + code + ". Supported languages: " + SUPPORTED_LANGUAGE);
            }
            this.code = noramalizedCode;
        }

    }

    public static Language defaultLanguage() {
        return new Language(DEFAULT_LANGUAGE);
    }

    public String getCode() {
        return this.code;
    }

    public String getDisplayName() {
        return switch (code) {
            case "en" -> "English";
            case "ua" -> "Українська";
            case "de" -> "Deutsch";
            case "fr" -> "Français";
            case "es" -> "Español";
            default -> code.toUpperCase();
        };
    }

    public boolean isSupported() {
        return SUPPORTED_LANGUAGE.contains(code);
    }

    public static Set<String> getSupportedLanguage() {
        return Set.copyOf(SUPPORTED_LANGUAGE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Language language = (Language) o;
        return Objects.equals(code, language.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code;
    }

}
