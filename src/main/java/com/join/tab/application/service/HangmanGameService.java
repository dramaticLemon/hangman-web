package com.join.tab.application.service;

import com.join.tab.application.dto.GameDto;
import com.join.tab.application.dto.GuessDto;

/**
 * Service interface for managing Hangman games.
 *
 * Provides methods to start a new game, make guesses, query the current game state,
 * and end a game for a given session.
 */
public interface HangmanGameService {

    /**
     * Starts a new Hangman game for the specified session.
     * If a game already exists for the session, it is replaced.
     *
     * @param sessionId the unique session identifier
     * @return a {@link GameDto} representing the initial state of the game
     */
    GameDto startNewGame(String sessionId);

    /**
     * Makes a guess for the specified session's current game.
     *
     * @param sessionId the unique session identifier
     * @param letter the letter being guessed
     * @return a {@link GuessDto} representing the result of the guess
     */
    GuessDto guessLetter(String sessionId, char letter);

    /**
     * Retrieves the current state of the game for the specified session.
     *
     * @param sessionId the unique session identifier
     * @return a {@link GameDto} representing the current game state,
     *         or null if no active game exists
     */
    GameDto getCurrentGame(String sessionId);

    /**
     * Ends the current game for the specified session.
     *
     * @param sessionId the unique session identifier
     */
    void endGame(String sessionId);
}