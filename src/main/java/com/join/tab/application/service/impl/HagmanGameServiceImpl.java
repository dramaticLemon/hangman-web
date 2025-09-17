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

    @Override
    public GameDto startNewGame(String sessionId) {
        GameId gameId = new GameId(sessionId);

        // Remove existing game if any
        gameRepository.delete(gameId);

        // Create new game
        HangmanGame game = gameFactory.createNewGame(gameId);
        gameRepository.save(game);

        return GameDto.fromDomain(game);
    }

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

    @Override
    public GameDto getCurrentGame(String sessionId) {
        GameId gameId = new GameId(sessionId);
        HangmanGame game = gameRepository.findById(gameId)
                .orElse(null);

        return game != null ? GameDto.fromDomain(game) : null;
    }

    @Override
    public void endGame(String sessionId) {
        GameId gameId = new GameId(sessionId);
        gameRepository.delete(gameId);
    }

}
