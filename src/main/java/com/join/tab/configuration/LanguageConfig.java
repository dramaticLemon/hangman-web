package com.join.tab.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class LanguageConfig {

    private final Set<String> supportedLanguage;
    private final String defaultLanguage;

    public LanguageConfig(
            @Value("${hangman.supported-language}") String supported,
            @Value("${hangman.default-language}") String defaultLanguage) {
        this.supportedLanguage = Stream.of(supported.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
        this.defaultLanguage = defaultLanguage;
    }

    public Set<String> getSupportedLanguage() {
        return supportedLanguage;
    }

    public String getDefaultLanguage() {
        return this.defaultLanguage;
    }
}
