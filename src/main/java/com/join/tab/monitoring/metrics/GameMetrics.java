package com.join.tab.monitoring.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Component that tracks metrics for Hangman game sessions.
 * <p>
 *     This class uses Micrometer to record:
 *     <ul>
 *         <li>Total games started, won, and lost</li>
 *         <li>Total letters guesses, correct and incorrect guesses</li>
 *         <li>Duration of game session</li>
 *         <li>Active game count (Gauge)</li>
 *         <li>Games grouped be language and category</li>
 *     </ul>
 * </p>
 *
 * <p>
 *     These metrics can be uses by Prometheus and Grafana for monitoring and
 *     visualization.
 * </p>
 */
@Component
public class GameMetrics {
    private static final Logger log = LoggerFactory.getLogger(GameMetrics.class);


    private final Counter gamesStartedCounter;
    private final Counter gamesWonCounter;
    private final Counter gamesLostCounter;
    private final Counter lettersGuessedCounter;
    private final Counter correctGuessesCounter;
    private final Counter incorrectGuessesCounter;
    private final Timer gameSessionTimer;

    private final AtomicLong activeGames = new AtomicLong(0);
    private final ConcurrentHashMap<String, AtomicLong> gamesByLanguage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> gamesByCategory = new ConcurrentHashMap<>();

    /**
     * Initializes all metrics and register them in the given MeterRegistry.
     * @param meterRegistry the registry to store metrics.
     */
    public GameMetrics(MeterRegistry meterRegistry) {
        // Counters for different game event
        this.gamesStartedCounter = Counter.builder("hangman.games.started")
                .description("Total number of games started")
                .register(meterRegistry);

        this.gamesWonCounter = Counter.builder("hangman.games.won")
                .description("Total number of games won by players")
                .register(meterRegistry);

        this.gamesLostCounter = Counter.builder("hangman.games.lost")
                .description("Total number of games lost by players")
                .register(meterRegistry);

        this.lettersGuessedCounter = Counter.builder("hangman.letters.guessed")
                .description("Total number of letters guessed")
                .register(meterRegistry);

        this.correctGuessesCounter = Counter.builder("hangman.letters.correct")
                .description("Total number of correct letter guesses")
                .register(meterRegistry);

        this.incorrectGuessesCounter = Counter.builder("hangman.letters.incorrect")
                .description("Total number of incorrect letter guesses")
                .register(meterRegistry);

        // Timer for game session duration
        this.gameSessionTimer = Timer.builder("hangman.game.session.duration")
                .description("Duration of game sessions")
                .register(meterRegistry);

        // Gauge for currently active games
        Gauge.builder("hangman.games.active", activeGames, AtomicLong::get)
                .description("Number of currently active games")
                .register(meterRegistry);
    }

    /**
     * Records the start of a new game.
     * Increments counters and active games, grouped by language and category
     */
    public void recordGameStarted(String language, String category) {
        gamesStartedCounter.increment();
        activeGames.incrementAndGet();

        gamesByLanguage.computeIfAbsent(language, k -> new AtomicLong(0)).incrementAndGet();
        if (category != null && !category.isEmpty()) {
            gamesByCategory.computeIfAbsent(category, k -> new AtomicLong(0)).incrementAndGet();
        }

        log.debug("Recorded game started: language={}, category={}", language, category);
    }

    /**
     * Record a game won by a player.
     * Decrements active games counter.
     */
    public void recordGameWon(String language) {
        gamesWonCounter.increment();
        activeGames.decrementAndGet();
        log.debug("Recorded game won: language={}", language);
    }

    /**
     * Records a game lost be a player.
     * Decrement active games counter.
     */
    public void recordGameLost(String language) {
        gamesLostCounter.increment();
        activeGames.decrementAndGet();
        log.debug("Recorded game lost: language={}", language);
    }

    /**
     * Record a letter guess and correct of incorrect counters.
     */
    public void recordLetterGuessed(String language, boolean correct) {
        lettersGuessedCounter.increment();

        if (correct) {
            correctGuessesCounter.increment();
        } else {
            incorrectGuessesCounter.increment();
        }

        log.debug("Recorded letter guess: language={}, correct={}", language, correct);
    }

    /** Start the given timer and records the duration in the game session timer */
    public Timer.Sample startGameTimer() {
        return Timer.start();
    }

    /**
     * Stops the given timer and records the duration in the game session timer.
     */
    public void recordGameDuration(Timer.Sample sample) {
        sample.stop(gameSessionTimer);
    }

    /**
     * Returns the number of currently active games.
     */
    private long getActiveGames() {
        return activeGames.get();
    }

    /** Return total games started count */
    public long getGamesStartedCount() {
        return (long) gamesStartedCounter.count();
    }

    /** Return total games won count */
    public long getGamesWonCount() {
        return (long) gamesWonCounter.count();
    }

    /** Return total games lose count */
    public long getGamesLostCount() {
        return (long) gamesLostCounter.count();
    }

    /** Returns the win rate as a double (0.0 to 1.0) */
    public double getWinRate() {
        long total = getGamesWonCount() + getGamesLostCount();
        return total > 0 ? (double) getGamesWonCount() / total : 0.0;
    }

}
