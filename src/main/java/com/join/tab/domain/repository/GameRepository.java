package com.join.tab.domain.repository;

import com.join.tab.domain.aggregate.HangmanGame;
import com.join.tab.domain.valueobject.GameId;

import java.util.Optional;

/**
 * Repository interface for managing {@link HangmanGame} aggregate.
 *
 * Provides basic CRUD operations for storing, retrieving, and deleting games.
 */
public interface GameRepository {

    /**
     * Saves or updated the given game.
     *
     * @param game the game to save.
     */
    void save(HangmanGame game);

    /**
     * Finds a game by its ID
     *
     * @param gameId the ID the game to find
     * @return an {@link Optional} containing the game if found, otherwise empty
     */
    Optional<HangmanGame> findById(GameId gameId);

    /**
     * Deletes gameId the ID of the game to delete
     *
     * @param gameId the ID of the game to delete
     */
    void delete(GameId gameId);
}
