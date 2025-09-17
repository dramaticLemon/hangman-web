package com.join.tab.infra.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "words", indexes = {
        @Index(name = "idx_word_length", columnList = "length"),
        @Index(name = "idx_word_category", columnList = "category")
})
@NoArgsConstructor
@AllArgsConstructor
public class WordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "value", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Word value cannot be blank")
    @Size(min = 3, max = 50, message = "Word must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Word must contain only letters")
    private String value;

    @Column(name = "length", nullable = false)
    private Integer length;

    @Column(name = "category", length = 30)
    @Size(max = 30, message = "Category must not exceed 30 characters")
    private String category;

    @Column(name = "difficulty_level")
    @Enumerated(EnumType.STRING)
    private DiffucultyLevel difficultlyLevel;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @PrePersist
    @PreUpdate
    private void validateAndNormalize() {
        if (value != null) {
            this.value = value.toLowerCase().trim();
            this.length = this.value.length();

            // Set difficulty based on length if not specified
            if (this.difficultlyLevel == null) {
                this.difficultlyLevel = determineDifficultyLevel(this.length);
            }
        }
    }

    private DiffucultyLevel determineDifficultyLevel(int wordLength) {
        if (wordLength <= 4) return DiffucultyLevel.EASY;
        if (wordLength <= 7) return DiffucultyLevel.MEDIUM;
        return DiffucultyLevel.HARD;
    }

    public enum DiffucultyLevel {
        EASY, MEDIUM, HARD;
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

    public DiffucultyLevel getDifficultlyLevel () {
        return difficultlyLevel;
    }

    public void setDifficultlyLevel (DiffucultyLevel difficultlyLevel) {
        this.difficultlyLevel = difficultlyLevel;
    }

    public Boolean getActive () {
        return isActive;
    }

    public void setIsActive (Boolean active) {
        isActive = active;
    }
}
