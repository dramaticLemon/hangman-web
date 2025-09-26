package com.join.tab.application.service;

import com.join.tab.application.dto.GameDto;
import com.join.tab.application.dto.GuessDto;
import com.join.tab.application.dto.LanguageInfoDto;

/**
 * Service interface for managing Hangman games.
 * Provides methods to start a new game, make guesses, query the current game state,
 * and end a game for a given session.
 */
public interface HangmanGameService {

    GameDto startNewGameWithLanguage(String sessionId, String languageCode);
    GameDto startNewGameWithPreferences(String sessionId, String languageCode, String category, String difficulty);

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

    /**
     * Returns detailed information about a specific language.
     *
     * @param languageCode the language code to retrieve info for
     * @return a {@link LanguageInfoDto} containing language details
     */
    LanguageInfoDto getLanguageInfo(String languageCode);

    /**
     * Returns information about all supported languages.
     * @return a {@link LanguageInfoDto} containing details for all languages.
     */
    LanguageInfoDto getAllLanguagesInfo();
}