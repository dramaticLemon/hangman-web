package com.join.tab.infra.entity;

import com.join.tab.domain.enums.DifficultyLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JPA entity representing a word in the Hangman game database.
 *
 * Stores the word value, its length, category, difficulty level, and active status.
 * Include validation and normalization logic before persisting or updating.
 *
 * Database mapping:
 * - Table name: "words"
 * - Indexes:
 *  - idx_word_length on "length"
 *  - idx_word_category on "category"
 *
 *  Features:
 *  - Enforces non-black, alphabetic word value of 3 - 50 characters.
 *  - Automatically calculates word length and normalizes value to lowercase.
 *  - Determined difficulty level based on word length if not explicitly set.
 *  - Supports options category and active status.
 */
@Entity
@Table(name = "words", indexes = {
        @Index(name = "idx_word_length", columnList = "length"),
        @Index(name = "idx_word_category", columnList = "category"),
        @Index(name = "idx_word_language", columnList = "language"),
        @Index(name = "idx_word_language_category", columnList = "language, category"),
        @Index(name = "idx_word_active_language", columnList =  "is_active, language")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_word_language", columnNames = {"value", "language"})
    })
@NoArgsConstructor
@AllArgsConstructor
public class WordEntity {

    /**
     * Unique identifier for the word.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The word value.
     * Must be alphabetic, 3-50 char, and unique.
     */
    @Column(name = "value", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Word value cannot be blank")
    @Size(min = 3, max = 50, message = "Word must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁіІїЇєЄґҐ]+$", message = "Word must contain only letters")
    private String value;

    @Column(name = "language", nullable = false, length = 5)
    @NotBlank(message = "Language cannot be blank")
    @Pattern(regexp = "^[a-z]{2}$", message = "Language must be a valid 2-letter code")
    private String language;

    /** Length of the word, automatically set based on value **/
    @Column(name = "length", nullable = false)
    private Integer length;

    /** Optional category fo the word, max 30 char **/
    @Column(name = "category", length = 30)
    @Size(max = 30, message = "Category must not exceed 30 characters")
    private String category;

    /** Difficulty level of the word (EASY, MEDIUM, HARD). **/
    @Column(name = "difficulty_level")
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficultlyLevel;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    /** Whether the word is active and available for use. Defaults to true. **/
    @Column(name = "is_active")
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        validateAndNormalize();
        this.createdAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        validateAndNormalize();
        this.updateAt = LocalDateTime.now();
    }

    private void validateAndNormalize() {
        if (value != null) {
            this.value = normalizeWordByLanguage(value, language);
            this.length = this.value.length();

            // Set difficulty based on length if not specified
            if (this.difficultlyLevel == null) {
                this.difficultlyLevel = determineDifficultyLevel(this.length);
            }
        }

        if (language != null) {
            this.language = language.toLowerCase().trim();
        }

        if (category != null) {
            this.category = category.toLowerCase().trim();
        }
    }

    private String normalizeWordByLanguage(String word, String lang) {
        if (word == null || lang == null ) return word;

        String normalized = word.trim();
        return switch (lang.toLowerCase()) {
            case "ua" -> normalized.toLowerCase();
            case "en", "de", "fr", "es" -> normalized.toLowerCase();
            default -> normalized.toLowerCase();
        };
    }

    /**
     * Determines difficulty level based on word length
     *
     * @param wordLength the length of the word
     * @return the corresponding {@link DifficultyLevel}
     */
    private DifficultyLevel determineDifficultyLevel(int wordLength) {
        if (wordLength <= 4) return DifficultyLevel.EASY;
        if (wordLength <= 7) return DifficultyLevel.MEDIUM;
        return DifficultyLevel.HARD;
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public String getValue () {
        return value;
    }

    public void setValue (String value) {
        this.value = value;
    }

    public Integer getLength () {
        return length;
    }

    public void setLength (Integer length) {
        this.length = length;
    }

    public String getCategory () {
        return category;
    }

    public void setCategory (String category) {
        this.category = category;
    }

    public DifficultyLevel getDifficultlyLevel () {
        return difficultlyLevel;
    }

    public void setDifficultlyLevel (DifficultyLevel difficultlyLevel) {
        this.difficultlyLevel = difficultlyLevel;
    }

    public Boolean getActive () {
        return isActive;
    }

    public void setIsActive (Boolean active) {
        isActive = active;
    }

    public String getLanguage () {
        return language;
    }

    public void setLanguage (String language) {
        this.language = language;
    }
}