package com.join.tab.domain.aggregate;

import com.join.tab.domain.event.GameEndedEvent;
import com.join.tab.domain.event.GameEvent;
import com.join.tab.domain.event.GameStartedEvent;
import com.join.tab.domain.event.LetterGuessedEvent;
import com.join.tab.domain.exception.InvalidGameStatusException;
import com.join.tab.domain.exception.LetterAlreadyGuessedException;
import com.join.tab.domain.model.GameId;
import com.join.tab.domain.model.Letter;
import com.join.tab.domain.model.Word;
import com.join.tab.domain.enums.GameStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HangmanGame {
    private static final int MAX_MISTAKES = 6;

    private final GameId gameId;
    private final Word word;
    private final Set<Letter> guessedLetters;
    private final List<GameEvent> events;
    private int mistakeCount;
    private GameStatus status;

    // constructor for new game
    public HangmanGame(GameId gameId, Word word) {
        this.gameId = gameId;
        this.word = word;
        this.guessedLetters = new HashSet<>();
        this.events = new ArrayList<>();
        this.mistakeCount = 0;
        this.status = GameStatus.IN_PROGRESS;

        addEvent(new GameStartedEvent(gameId, word.getValue()));
    }

    // Constructor for loading existing game (from repository)
    public HangmanGame (GameId gameId, Word word, Set<Letter> guessedLetters,
                        int mistakeCount, GameStatus status) {
        this.gameId = gameId;
        this.word = word;
        this.guessedLetters = new HashSet<>(guessedLetters);
        this.events = new ArrayList<>();
        this.mistakeCount = mistakeCount;
        this.status = status;
    }

    public GuessResult guessResult(Letter letter) {
        validateGameInProgress();
        validateLetterNotGuesse(letter);

        guessedLetters.add(letter);
        boolean isCorrect = word.contains(letter.getValue());

        if (!isCorrect) {
            mistakeCount++;
        }

        updateGameStatus();
        addEvent(new LetterGuessedEvent(gameId, letter, isCorrect));

        if (status != GameStatus.IN_PROGRESS) {
            addEvent(new GameEndedEvent(gameId, status == GameStatus.WON, word.getValue()));
        }

        return new GuessResult(
                getCurrentState(),
                getRemainingTries(),
                status,
                isCorrect,
                hasGuessedLetter(letter)
        );
    }

    private void validateGameInProgress() {
        if (status != GameStatus.IN_PROGRESS) {
            throw new InvalidGameStatusException("Game is already finished");
        }
    }

    private void validateLetterNotGuesse(Letter letter) {
        if (hasGuessedLetter(letter)) {
            throw new LetterAlreadyGuessedException(letter.getValue());
        }
    }

    public boolean hasGuessedLetter(Letter letter) {
        return guessedLetters.contains(letter);
    }

    private void updateGameStatus() {
        if (mistakeCount >= MAX_MISTAKES) {
            status = GameStatus.LOST;
        } else if(isWordCompletelyGuessed()){
            status = GameStatus.WON;
        }
    }

    private boolean isWordCompletelyGuessed() {
        for (int i = 0; i < word.getLength(); i++) {
            Letter wordLetter = new Letter(word.getCharAt(i));
            if (!guessedLetters.contains(wordLetter)) {
                return false;
            }
        }
        return true;
    }

    public String getCurrentState() {
        StringBuilder state = new StringBuilder();
        for (int i = 0; i < word.getLength(); i++) {
            char ch = word.getCharAt(i);
            Letter letter = new Letter(ch);
            if (guessedLetters.contains(letter)) {
                state.append(ch);
            } else {
                state.append('_');
            }
        }
        return state.toString();
    }

    private void addEvent(GameEvent event) {
        events.add(event);
    }

    public GameId getGameId() {
        return gameId;
    }

    public String getWord() {
        return word.getValue();
    }

    public GameStatus getStatus() {
        return status;
    }

    public int getRemainingTries() {
        return MAX_MISTAKES - mistakeCount;
    }

    public int getMistakeCount() {
        return mistakeCount;
    }

    public Set<Letter> getGuessedLetters() {
        return new HashSet<>(guessedLetters);
    }

    public boolean isWon() {
        return status == GameStatus.WON;
    }

    public boolean isLost() {
        return status == GameStatus.LOST;
    }

    public boolean isInProgress() {
        return status == GameStatus.IN_PROGRESS;
    }

    public List<GameEvent> getUncommittedEvents() {
        return new ArrayList<>(events);
    }

    public void clearEvents() {
        events.clear();
    }
}
