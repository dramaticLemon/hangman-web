package com.join.tab.domain.valueobject;

import com.join.tab.domain.enums.DifficultyLevel;

import java.util.Objects;

/**
 * This class just a container for user's game preferences (language, category,
 * difficulty). It also provides helper methods to check if category/difficulty
 * were set.
 */
public class GamePreferences {
    private final Language language;
    private final String category;
    private final DifficultyLevel difficulty;

    /**
     * Create preferences with a language only.
     * If language is null, the default language is uses.
     *
     * @param language the game language.
     */
    public GamePreferences(Language language) {
        this(language, null, null);
    }

    /**
     * Creates preferences with a language and category.
     *
     * @param language the game language.
     * @param category the word category.
     */
    public GamePreferences(Language language, String category) {
        this(language, category, null);
    }

    /**
     * Create pref. with a lang., cat., and difficulty.
     *
     * @param language the game language.
     * @param category tha word category.
     * @param difficulty the difficulty level(can be null)
     */
    public GamePreferences(Language language, String category, DifficultyLevel difficulty) {
        this.language = language != null ? language : Language.defaultLanguage();
        this.category = category;
        this.difficulty = difficulty;
    }

    /**
     * Create pref. with default language.
     * @return default pref.
     */
    public static GamePreferences defaultPreferences() {
        return new GamePreferences(Language.defaultLanguage());
    }

    /**
     * Create pref. with specific lang.
     * @param language the game lang.
     * @return pref. with given lang.
     */
    public static GamePreferences withLanguage(Language language) {
        return new GamePreferences(language);
    }

    public Language getLanguage() {
        return language;
    }

    public String getCategory() {
        return category;
    }

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    public boolean hasCategory() {
        return category != null && !category.trim().isEmpty();
    }

    public boolean hasDifficulty() {
        return difficulty != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GamePreferences that = (GamePreferences) o;
        return Objects.equals(language, that.language) &&
                Objects.equals(category, that.category) &&
                difficulty == that.difficulty;
    }

    @Override
    public int hashCode() {
        return Objects.hash(language, category, difficulty);
    }

}
