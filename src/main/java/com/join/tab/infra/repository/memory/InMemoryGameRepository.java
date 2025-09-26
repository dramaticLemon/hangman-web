package com.join.tab.infra.repository.memory;

import com.join.tab.domain.aggregate.HangmanGame;
import com.join.tab.domain.enums.DifficultyLevel;
import com.join.tab.domain.valueobject.GameId;
import com.join.tab.domain.valueobject.GamePreferences;
import com.join.tab.domain.valueobject.Language;
import com.join.tab.domain.valueobject.Letter;
import com.join.tab.domain.model.Word;
import com.join.tab.domain.repository.GameRepository;
import com.join.tab.domain.enums.GameStatus;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of {@link GameRepository}.
 * Stores Hangman games in a concurrent hash map, suitable for testing or
 * temporary storage without a persistent database.
 * Provides basic CRUD operations for saving, retrieving, and deleting games.
 * Uses an internal ({@link GameData}) class to hold the game states.
 */
@Repository
public class InMemoryGameRepository implements GameRepository {

    private final Map<GameId, GameData> games = new ConcurrentHashMap<>();

    /**
     * Saves or updates a Hangman game in memory.
     * @param game the game to save.
     */
    @Override
    public void save(HangmanGame game) {
        GameData gameData = new GameData(game);
        games.put(game.getGameId(), gameData);
    }

    /**
     * Finds a game by its ID.
     * @param gameId the ID the game to find
     * @return an {@link Optional} containing the game if found, otherwise empty
     */
    @Override
    public Optional<HangmanGame> findById(GameId gameId){
        GameData gameData = games.get(gameId);
        if (gameData == null) {
            return Optional.empty();
        }

        Word word = new Word(gameData.getWord(), new Language(gameData.getLanguage()));
        GamePreferences preferences = new GamePreferences(
                new Language(gameData.getLanguage()),
                gameData.getCategory(),
                gameData.getDifficulty() != null ? DifficultyLevel.valueOf(gameData.getDifficulty()) : null
        );

        HangmanGame game = new HangmanGame(
                gameId,
                word,
                preferences,
                gameData.getGuessedLetters(),
                gameData.getMistakeCount(),
                gameData.getStatus()
        );

        return Optional.of(game);
    }

    /**
     * Delete a game by its ID.
     * @param gameId the ID of the game to delete
     */
    @Override
    public void delete(GameId gameId) {
        games.remove(gameId);
    }

    /** Internal data class to store game state in memory **/
    private static class GameData {
        private final String word;
        private final Set<Letter> guessedLetters;
        private final int mistakeCount;
        private final GameStatus status;
        private final String language;     // новый
        private final String category;     // новый
        private final String difficulty;   // новый

        public GameData(HangmanGame game) {
            this.word = game.getWord();
            this.guessedLetters = new HashSet<>(game.getGuessedLetters());
            this.mistakeCount = game.getMistakeCount();
            this.status = game.getStatus();
            this.language = game.getPreferences().getLanguage().getCode();
            this.category = game.getPreferences().getCategory();
            this.difficulty = game.getPreferences().getDifficulty() != null
                    ? game.getPreferences().getDifficulty().name()
                    : null;
        }

        public String getWord() { return word; }
        public Set<Letter> getGuessedLetters() { return guessedLetters; }
        public int getMistakeCount() { return mistakeCount; }
        public GameStatus getStatus() { return status; }
        public String getLanguage() { return language; }
        public String getCategory() { return category; }
        public String getDifficulty() { return difficulty; }
    }

}