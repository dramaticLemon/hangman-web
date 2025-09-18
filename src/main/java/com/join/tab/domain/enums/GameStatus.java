package com.join.tab.domain.enums;

/**
 * Represent the current status of a Hangman game.
 *
 * Response values:
 * - {@link #IN_PROGRESS}: The game is ongoing and the player can continue guessing letters.
 * - {@link #WON}: The player has successfully guessed all letters in the word.
 * - {@link #LOST}: The player has run out of attempts and the game is over.
 *
 * Usage:
 * <pre>
 *     GameStatus status = GameStatus.IN_PROGRESS;
 *     if (status == GameStatus.WON) {
 *         // Handle win condition
 *     }
 * </pre>
 */
public enum GameStatus {
    IN_PROGRESS,
    WON,
    LOST
}
