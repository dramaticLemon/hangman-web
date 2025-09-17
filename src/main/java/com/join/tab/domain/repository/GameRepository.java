package com.join.tab.domain.repository;

import com.join.tab.domain.aggregate.HangmanGame;
import com.join.tab.domain.model.valueobject.GameId;

import java.util.Optional;

public interface GameRepository {
    void save(HangmanGame game);
    Optional<HangmanGame> findById(GameId gameId);
    void delete(GameId gameId);
}
