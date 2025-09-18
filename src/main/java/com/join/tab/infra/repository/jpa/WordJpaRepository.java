package com.join.tab.infra.repository.jpa;

import com.join.tab.infra.entity.WordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for managing {@link WordEntity} objects in the database.
 *
 * Provides methods to fetch random words, filter by difficulty, length, or category,
 * find by value, list active words, and retrieve statistics such as categories or total count.
 *
 * Extend {@link  JpaRepository} to provide standard CRUD operation.
 */

public interface WordJpaRepository extends JpaRepository<WordEntity, Long> {

    /**
     * Finds a single random active word.
     *
     * @return an {@link Optional} containing a random active {@link WordEntity} or empty if none exists
     */
    @Query(value = "SELECT * FROM words WHERE is_active = true ORDER BY RANDOM() LIMIT 1",
            nativeQuery = true)
    Optional<WordEntity> findRandomWord();

    /**
     * Finds a single random active word with the specified difficulty level.
     *
     * @param level the difficulty leve (EASY, MEDIUM, HARD)
     * @return an {@link Optional} containing a random matching {@link WordEntity}, or empty if none exists
     */
    @Query(value = "SELECT * FROM words WHERE is_active = true AND difficulty_level = :level ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<WordEntity> findRandomWordByDifficulty(@Param("level") String level);

    /**
     * Finds random active word within a specified length range.
     *
     * @param minLength the min length of the word
     * @param maxLengthc the max length of the word
     * @return a list of {@link WordEntity} objects matching the criteria
     */
    @Query("SELECT w FROM WordEntity w WHERE w.isActive = true AND w.length BETWEEN :minLength AND :maxLength ORDER BY FUNCTION('RANDOM')")
    List<WordEntity> findRandomWordByLength(
            @Param("minLength") int minLength,
            @Param("maxLength") int maxLengthc
    );

    /**
     * Finds random active word belonging to a specific category.
     *
     * @param category the word category
     * @return a list of {@link WordEntity} objects in the given category
     */
    @Query("SELECT w FROM WordEntity w WHERE w.isActive = true AND w.category = :category ORDER BY FUNCTION('RANDOM')")
    List<WordEntity> findRandomWordByCategory(@Param("category") String category);

    /**
     * Finds a word by its value, ignoring case.
     *
     * @param value the word to search for
     * @return an {@link Optional} containing the {@link WordEntity}, or empty if not found
     */
    Optional<WordEntity> findByValueIgnoreCase(String value);

    /**
     * Retrieves all active words ordered alphabetically by value.
     *
     * @return a list of active {@link WordEntity} objects ordered by value
     */
    List<WordEntity> findByIsActiveTrueOrderByValueAsc();

    /**
     * Retrieves all distinct active categories.
     *
     * @return al list of category names
     */
    @Query("SELECT DISTINCT  w.category FROM WordEntity w WHERE w.category IS NOT NULL AND w.isActive = true")
    List<String> findAllCategories();

    /**
     * Counts the total number of active words.
     *
     * @return the count of active word
     */
    long countByIsActiveTrue();
}
