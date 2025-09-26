package com.join.tab.monitoring.info;

import com.join.tab.infra.repository.jpa.WordJpaRepository;
import com.join.tab.monitoring.metrics.GameMetrics;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Contributes game and word statistics to the Spring Boot Actuator / info endpoint
 * <p>
 *     This class collect metrics such as total games started, won, lost, win rate,
 *     as wel as word counts and supported languages form the database.
 * </p>
 */
@Component
public class GameStatsInfoContributor implements InfoContributor {

    private final GameMetrics gameMetrics;
    private final WordJpaRepository wordJpaRepository;

    /**
     * Constructor to inject game metrics and word repository.
     *
     * @param gameMetrics metrics about game play.
     * @param wordJpaRepository repo for accessing word data
     */
    public GameStatsInfoContributor (GameMetrics gameMetrics, WordJpaRepository wordJpaRepository) {
        this.gameMetrics = gameMetrics;
        this.wordJpaRepository = wordJpaRepository;
    }

    /**
     * Adds detailed game and word stat. to the Actuator info builder.
     * @param builder the Inro.Builder provided by Spring Boot Actuator
     */
    @Override
    public void contribute (Info.Builder builder) {
       Map<String, Object> gameStats = new HashMap<>();

        // Game statistics
        gameStats.put("totalGamesStarted", gameMetrics.getGamesStartedCount());
        gameStats.put("totalGamesWon", gameMetrics.getGamesWonCount());
        gameStats.put("totalGamesLost", gameMetrics.getGamesLostCount());
        gameStats.put("winRate", String.format("%.2f%%", gameMetrics.getWinRate() * 100));

        // Word statistics
        gameStats.put("totalWords", wordJpaRepository.count());
        gameStats.put("activeWords", wordJpaRepository.countByIsActiveTrue());
        gameStats.put("supportedLanguages", wordJpaRepository.findSupportedLanguages());

        builder.withDetail("gameStatistics", gameStats);

    }
}
