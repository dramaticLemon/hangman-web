package com.join.tab.application.service.impl;

import com.join.tab.domain.aggregate.HangmanGame;
import com.join.tab.domain.exception.GameNotFoundException;
import com.join.tab.domain.model.valueobject.GameId;
import com.join.tab.domain.model.valueobject.Letter;
import com.join.tab.domain.repository.GameRepository;
import com.join.tab.domain.service.GameFactory;
import com.join.tab.application.dto.GameDto;
import com.join.tab.application.dto.GuessDto;
import com.join.tab.application.service.HangmanGameService;
import org.springframework.stereotype.Service;

@Service
public class HagmanGameServiceImpl implements HangmanGameService {

    private final GameRepository gameRepository;
    private final GameFactory gameFactory;

    public HagmanGameServiceImpl(GameRepository gameRepository, GameFactory gameFactory) {
        this.gameRepository = gameRepository;
        this.gameFactory = gameFactory;
    }

    /**
     * Starts a new Hangman game for the given session.
     * Delete any existing game for the session.
     * Creates a new game using the GameFactory.
     * Saves the new game to the repository.
     * @param sessionId the unique identifier of the user's session.
     * @return a GameDto representing the new game.
     */
    @Override
    public GameDto startNewGame(String sessionId) {
        GameId gameId = new GameId(sessionId);
        gameRepository.delete(gameId);
        HangmanGame game = gameFactory.createNewGame(gameId);
        gameRepository.save(game);

        return GameDto.fromDomain(game);
    }

    /**
     * Mekes a guess in the current Hangman game for the given session.
     *
     * Steps performed:
     * 1. Finds the game associated with the session ID.
     * 2. Throws {@link GameNotFoundException} if no game is found.
     * 3. Converts the input character to a {@link Letter} value object.
     * 4. Checks if the guessed letter is correct using the game logic.
     * 5. Saves the updated game state in the repository.
     * 6. Returns a {@link GuessDto} containing the updated game state and the result of the guess.
     *
     * @param sessionId the unique identifier of the user's session.
     * @param letter the character being guessed.
     * @return a {@link GuessDto} containing the updated game and guess result
     * @throws  GameNotFoundException if not game exists for the given session
     */
    @Override
    public GuessDto guessLetter(String sessionId, char letter) {
        GameId gameId = new GameId(sessionId);
        HangmanGame game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game not found for session: " + sessionId));

        Letter domainLetter = new Letter(letter);
        HangmanGame.GuessResult result = game.guessResult(domainLetter);

        gameRepository.save(game);

        return GuessDto.fromDomain(game, result);
    }

    /**
     * Retrieves the current Hangman game for the given session.
     *
     * Steps performed:
     * 1. Finds the game associated with the session ID.
     * 2. Returns the game wrapped as a {@link GameDto} if it exists.
     * 3. Returns {@code null} if no game is found for the session.
     *
     * @param sessionId the unique identifier of the user's session.
     * @return a {@link GameDto} representing the current game, or {@code null} if no game exists.
     */
    @Override
    public GameDto getCurrentGame(String sessionId) {
        GameId gameId = new GameId(sessionId);
        HangmanGame game = gameRepository.findById(gameId)
                .orElse(null);

        return game != null ? GameDto.fromDomain(game) : null;
    }

    /**
     * Ends the current Hangman game for the given session.
     *
     * Steps performed:
     * 1. Finds the game associated with the session ID
     * 2. Deleted the game form the repository.
     *
     * @param sessionId the unique identifier of the user's session.
     */
    @Override
    public void endGame(String sessionId) {
        GameId gameId = new GameId(sessionId);
        gameRepository.delete(gameId);
    }

}
