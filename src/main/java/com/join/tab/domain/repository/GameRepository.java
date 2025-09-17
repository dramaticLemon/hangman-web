package com.join.tab.domain.repository;

import com.join.tab.domain.aggregate.HangmanGame;
import com.join.tab.domain.model.GameId;

import java.util.Optional;

public interface GameRepository {
    void save(HangmanGame game);
    Optional<HangmanGame> findById(GameId gameId) throws IllegalAccessException;
    void delete(GameId gameId);
}
