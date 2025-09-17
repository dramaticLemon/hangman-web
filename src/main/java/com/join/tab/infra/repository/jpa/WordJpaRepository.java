package com.join.tab.infra.repository.jpa;

import com.join.tab.infra.entity.WordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WordJpaRepository extends JpaRepository<WordEntity, Long> {

    @Query(value = "SELECT * FROM words WHERE is_active = true ORDER BY RANDOM() LIMIT 1",
            nativeQuery = true)
    Optional<WordEntity> findRandomWord();

    @Query(value = "SELECT * FROM words WHERE is_active = true AND difficulty_level = :level ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<WordEntity> findRandomWordByDifficulty(@Param("level") String level);

    @Query("SELECT w FROM WordEntity w WHERE w.isActive = true AND w.length BETWEEN :minLength AND :maxLength ORDER BY FUNCTION('RANDOM')")
    List<WordEntity> findRandomWordByLength(
            @Param("minLength") int minLength,
            @Param("maxLength") int maxLengthc
    );

    @Query("SELECT w FROM WordEntity w WHERE w.isActive = true AND w.category = :category ORDER BY FUNCTION('RANDOM')")
    List<WordEntity> findRandomWordByCategory(@Param("category") String category);

    Optional<WordEntity> findByValueIgnoreCase(String value);

    List<WordEntity> findByIsActiveTrueOrderByValueAsc();

    @Query("SELECT DISTINCT  w.category FROM WordEntity w WHERE w.category IS NOT NULL AND w.isActive = true")
    List<String> findAllCategories();

    long countByIsActiveTrue();
}
