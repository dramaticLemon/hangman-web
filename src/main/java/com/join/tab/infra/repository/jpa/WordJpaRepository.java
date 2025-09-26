package com.join.tab.infra.repository.jpa;

import com.join.tab.infra.entity.WordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for managing {@link WordEntity} objects in the database.
 * Provides methods to fetch random words, filter by difficulty, length, or category,
 * find by value, list active words, and retrieve statistics such as categories or total count.
 * Extend {@link  JpaRepository} to provide standard CRUD operation.
 */
public interface WordJpaRepository extends JpaRepository<WordEntity, Long> {

    /**
     * Finds a single random active word with the specified difficulty level.
     *
     * @param level the difficulty leve (EASY, MEDIUM, HARD)
     * @return an {@link Optional} containing a random matching
     * {@link WordEntity}, or empty if none exists
     */
    @Query(value = """
        SELECT * 
        FROM words
        WHERE is_active = true
          AND difficulty_level = :level
        ORDER BY RANDOM()
        LIMIT 1
        """, nativeQuery = true)
    Optional<WordEntity> findRandomWordByDifficulty(@Param("level") String level);

    /**
     * Finds random active word belonging to a specific category.
     *
     * @param category the word category
     * @return a list of {@link WordEntity} objects in the given category
     */
    @Query("""
        SELECT w
        FROM WordEntity w
        WHERE
            w.isActive = true
            AND
            w.category = :category
        ORDER BY FUNCTION('RANDOM')
        """)
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

    /** Random word by language */
    @Query(value = """
            SELECT *
            FROM words
            WHERE
                is_active = true
                AND
                language = :language
            ORDER BY RANDOM()
            LIMIT 1
            """, nativeQuery = true)
    Optional<WordEntity> findRandomWordByLanguage(@Param("language") String language);

    /** Random word by language and difficulty */
    @Query(value = """
            SELECT *
            FROM words
            WHERE
                is_active = true
                AND language = :language
                AND difficulty_level = :level
            ORDER BY RANDOM()
            LIMIT 1
            """, nativeQuery = true)
    Optional<WordEntity> findRandomWordByLanguageAndDifficulty(
            @Param("language") String language,
            @Param("level") String level);

    /** Random word by language and category */
    @Query(value = """
            SELECT *
            FROM words
            WHERE
                is_active = true
                AND language = :language
                AND category = :category
            ORDER BY RANDOM()
            LIMIT 1
            """, nativeQuery = true)
    Optional<WordEntity> findRandomWordByLanguageAndCategory(
            @Param("language") String language,
            @Param("category") String category);

    /** Random word by all criteria */
    @Query(value = """
            SELECT *
            FROM words
            WHERE
                is_active = true
                AND language = :language
                AND (:category IS NULL OR category = :category)
                AND (:level IS NULL OR difficulty_level = :level)
            ORDER BY RANDOM()
            LIMIT 1
            """, nativeQuery = true)
    Optional<WordEntity> findRandomWordByCriteria(
            @Param("language") String language,
            @Param("category") String category,
            @Param("level") String level
    );

    /**  Find words by language and length range */
    @Query("""
            SELECT w
            FROM WordEntity w
            WHERE
                w.isActive = true
                AND w.language = :language
                AND w.length BETWEEN :minLength
                AND :maxLength
            ORDER BY FUNCTION('RANDOM')
            """)
    List<WordEntity> findWordsByLanguageAndLength(
            @Param("language") String language,
            @Param("minLength") int minLength,
            @Param("maxLength") int maxLength
    );

    /**  Check if word exists for specific language */
    Optional<WordEntity> findByValueIgnoreCaseAndLanguage(
            String value, String language);

    /** Get all active words by language */
    List<WordEntity> findByLanguageAndIsActiveTrueOrderByValueAsc(String language);

    /**  Get categories for specific language */
    @Query("""
            SELECT DISTINCT w.category
            FROM WordEntity w
            WHERE
                w.category IS NOT NULL
                AND w.isActive = true
                AND w.language = :language""")
    List<String> findCategoriesByLanguage(@Param("language") String language);

    /** get supported languages */
    @Query("""
           SELECT DISTINCT w.language
           FROM WordEntity w
           WHERE
                w.isActive = true
           ORDER BY w.language""")
    List<String> findSupportedLanguages();

    /**
     * Retrieves all distinct active categories.
     *
     * @return al list of category names
     */
    @Query("""
        SELECT DISTINCT w.category
        FROM WordEntity w
        WHERE
            w.category IS NOT NULL
            AND w.isActive = true""")
    List<String> findAllCategories();

    /**
     * Counts the total number of active words.
     *
     * @return the count of active word
     */
    long countByIsActiveTrue();

    /** count words by language */
    long countByLanguageAndIsActiveTrue(String language);

    /** count words be language and category */
    long countByLanguageAndCategoryAndIsActiveTrue(
            String language, String category);

    /**  fallback - get any random word if no words found for specific language */
    @Query(value = """
        SELECT *
        FROM words
        WHERE is_active = true
        ORDER BY RANDOM()
        LIMIT 1""", nativeQuery = true)
    Optional<WordEntity> findAnyRandomWord();
}
