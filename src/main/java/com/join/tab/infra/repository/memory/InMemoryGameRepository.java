package com.join.tab.infra.repository.memory;

import com.join.tab.domain.aggregate.HangmanGame;
import com.join.tab.domain.model.GameId;
import com.join.tab.domain.model.Letter;
import com.join.tab.domain.model.Word;
import com.join.tab.domain.repository.GameRepository;
import com.join.tab.domain.enums.GameStatus;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryGameRepository implements GameRepository {

    private final Map<GameId, GameData> games = new ConcurrentHashMap<>();

    @Override
    public void save(HangmanGame game) {
        GameData gameData = new GameData(
                game.getWord(),
                game.getGuessedLetters(),
                game.getMistakeCount(),
                game.getStatus()
        );
        games.put(game.getGameId(), gameData);
    }

    @Override
    public Optional<HangmanGame> findById(GameId gameId) throws IllegalAccessException {
        GameData gameData = games.get(gameId);
        if (gameData == null) {
            return Optional.empty();
        }

        Word word = new Word(gameData.getWord());
        HangmanGame game = new HangmanGame(
                gameId,
                word,
                gameData.getGuessedLetters(),
                gameData.getMistakeCount(),
                gameData.getStatus()
        );

        return Optional.of(game);
    }

    @Override
    public void delete(GameId gameId) {
        games.remove(gameId);
    }

    // Internal data class for persistence
    private static class GameData {
        private final String word;
        private final Set<Letter> guessedLetters;
        private final int mistakeCount;
        private final GameStatus status;

        public GameData(String word, Set<Letter> guessedLetters, int mistakeCount, GameStatus status) {
            this.word = word;
            this.guessedLetters = new HashSet<>(guessedLetters);
            this.mistakeCount = mistakeCount;
            this.status = status;
        }

        public String getWord() { return word; }
        public Set<Letter> getGuessedLetters() { return guessedLetters; }
        public int getMistakeCount() { return mistakeCount; }
        public GameStatus getStatus() { return status; }
    }
}