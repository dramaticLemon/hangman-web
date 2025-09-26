package com.join.tab.controller;

import com.join.tab.infra.repository.jpa.WordJpaRepository;
import com.join.tab.monitoring.metrics.GameMetrics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Rest controller for exposing game and word statistics for monitoring purposes.
 * <p>
 *     Provides detailed matrics such as game started, won, lost, win rate,
 *     total words, active words, and language distribution.
 *     The starts can be accessed via GET /api/monitoring/stats
 * </p>
 */
@RestController
@RequestMapping("/api/monitoring")
public class MonitoringController {

    private final GameMetrics gameMetrics;
    private final WordJpaRepository wordJpaRepository;

    public MonitoringController (GameMetrics gameMetrics, WordJpaRepository wordJpaRepository) {
        this.gameMetrics = gameMetrics;
        this.wordJpaRepository = wordJpaRepository;
    }

    @GetMapping("/stats")
    public Map<String, Object> getDetailedStats() {
        Map<String, Object> stats = new HashMap<>();

        // Game metrics
        Map<String, Object> gameStats = new HashMap<>();
        gameStats.put("gamesStarted", gameMetrics.getGamesStartedCount());
        gameStats.put("gamesWon", gameMetrics.getGamesWonCount());
        gameStats.put("gamesLost", gameMetrics.getGamesLostCount());
        gameStats.put("winRate", gameMetrics.getWinRate());

        // Word metrics
        Map<String, Object> wordStats = new HashMap<>();
        wordStats.put("totalWords", wordJpaRepository.count());
        wordStats.put("activeWords", wordJpaRepository.countByIsActiveTrue());
        wordStats.put("languageDistribution", getLanguageDistribution());

        stats.put("games", gameStats);
        stats.put("words", wordStats);
        stats.put("timestamp", System.currentTimeMillis());

        return stats;
    }

    private Map<String, Long> getLanguageDistribution() {
        Map<String, Long> distribution = new HashMap<>();
        wordJpaRepository.findSupportedLanguages().forEach(lang ->
                distribution.put(lang, wordJpaRepository.countByLanguageAndIsActiveTrue(lang)));
        return distribution;
    }
}
