package com.join.tab.domain.aggregate;

import com.join.tab.domain.event.GameEndedEvent;
import com.join.tab.domain.event.GameEvent;
import com.join.tab.domain.event.GameStartedEvent;
import com.join.tab.domain.event.LetterGuessedEvent;
import com.join.tab.domain.exception.InvalidGameStatusException;
import com.join.tab.domain.exception.LetterAlreadyGuessedException;
import com.join.tab.domain.valueobject.GameId;
import com.join.tab.domain.valueobject.GamePreferences;
import com.join.tab.domain.valueobject.Language;
import com.join.tab.domain.valueobject.Letter;
import com.join.tab.domain.model.Word;
import com.join.tab.domain.enums.GameStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Aggregate root representing a Hangman game.
 *
 * Encapsulate the game state, guessed letters. mistake count, and domain events.
 * Provides constructors for starting a new game or loading an existing game form a repository.
 *
 * Key features:
 * - Track the word to guess, guessed letters, mistakes, and game status.
 * - Publishes domain events such as {@link GameStartedEvent}, {@link LetterGuessedEvent}, and {@link GameEndedEvent}.
 * - Supports gameplay operations like guessing letters and retrieving the current word state.
 *
 * Usage:
 * <pre>
 *     HangmanGame game = new HangmanGame(new GameId("ex123"), new Word("ex"));
 * </pre>
 */
public class HangmanGame {
    private static final int MAX_MISTAKES = 6;

    private final GameId gameId;
    private final Word word;
    private final GamePreferences preferences;
    private final Set<Letter> guessedLetters;
    private final List<GameEvent> events;
    private int mistakeCount;
    private GameStatus status;

    /**
     * Creates a new Hangman game with a fresh word.
     *
     * @param gameId the unique ID for the game.
     * @param word the word to guess in the game.
     */
    public HangmanGame(GameId gameId, Word word, GamePreferences gamePreferences) {
        this.gameId = gameId;
        this.word = word;
        this.preferences = gamePreferences;
        this.guessedLetters = new HashSet<>();
        this.events = new ArrayList<>();
        this.mistakeCount = 0;
        this.status = GameStatus.IN_PROGRESS;

        addEvent(new GameStartedEvent(
                gameId,
                word.getValue(),
                word.getLanguage(),
                preferences.getCategory()
                ));
    }

    /**
     * Loads an existing Hangman game (e.g., from repo) with this current state.
     *
     * @param gameId the unique ID of the game
     * @param word the word to guess
     * @param guessedLetters the set of letters already guessed
     * @param mistakeCount the number of incorrect guessed so far
     * @param status the current status of game.
     */
    public HangmanGame (GameId gameId, Word word, GamePreferences preferences, Set<Letter> guessedLetters,
                        int mistakeCount, GameStatus status) {
        this.gameId = gameId;
        this.word = word;
        this.preferences = preferences;
        this.guessedLetters = new HashSet<>(guessedLetters);
        this.events = new ArrayList<>();
        this.mistakeCount = mistakeCount;
        this.status = status;
    }

    /**
     * Process a player's guess for a letter in the current game.
     *
     * Steps performed:
     * 1. Validated that the game is still in progress and the letter was not guessed before.
     * 2. Adds the letter to the set of guessed letters.
     * 3. Updates mistake count if the guess is incorrect.
     * 4. Updates the game status (won, lost or in_progress).
     * 5. Records corresponding game events (letter guessed, and gama ended if applicable).
     *
     * @param letter letter the guessed {@link Letter}
     * @return a {@link GuessResult} containing the updated game state and guess outcome
     * @throws IllegalStateException if the game is already over.
     * @throws IllegalArgumentException if the letter was already guessed
     */
    public GuessResult guessResult(Letter letter) {
        validateGameInProgress();
        validateLetterForLanguage(letter);
        validateLetterNotGuessed(letter);

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
                hasGuessedLetter(letter),
                word.getLanguage()
        );
    }

    /**
     * Ensures that the game is still in progress.
     *
     * @throws InvalidGameStatusException if the game has already ended
     */
    private void validateGameInProgress() {
        if (status != GameStatus.IN_PROGRESS) {
            throw new InvalidGameStatusException("Game is already finished");
        }
    }

    private void validateLetterForLanguage(Letter letter) {
        if (!isValidLetterForLanguage(letter.getValue(), word.getLanguage())) {
            throw new IllegalArgumentException(
                    String.format("Letter '%c' is not valid for language '%s'",
                            letter.getValue(), word.getLanguage().getCode()));
        }
    }

    private boolean isValidLetterForLanguage(char letter, Language language) {
        return switch (language.getCode()) {
            case "ua" -> isCyrillicLetter(letter);
            case "en", "de", "fr", "es" -> isLatinLetter(letter);
            default  -> isLatinLetter(letter) || isCyrillicLetter(letter);
        };
    }

    private boolean isLatinLetter(char letter) {
        return (letter >= 'a' && letter <= 'z') || (letter >= 'A' && letter <= 'Z');
    }

    private boolean isCyrillicLetter(char letter) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(letter);
        return block == Character.UnicodeBlock.CYRILLIC
                || block == Character.UnicodeBlock.CYRILLIC_SUPPLEMENTARY
                || block == Character.UnicodeBlock.CYRILLIC_EXTENDED_A
                || block == Character.UnicodeBlock.CYRILLIC_EXTENDED_B;
    }
    /**
     * Ensures that the given letter has not been guessed before in the current game.
     *
     * @param letter the letter to validate
     * @throws LetterAlreadyGuessedException if the letter was already guessed
     */
    private void validateLetterNotGuessed (Letter letter) {
        if (hasGuessedLetter(letter)) {
            throw new LetterAlreadyGuessedException(letter.getValue());
        }
    }

    /**
     * Check if the letter has already been guessed in the current game.
     *
     * @param letter the letter to check
     * @return {@code ture} if the letter has been guessed, {@code false} otherwise
     */
    public boolean hasGuessedLetter(Letter letter) {
        return guessedLetters.contains(letter);
    }

    /**
     * Updated the status of the game based on the current state.
     *
     * Sets the status to {@link GameStatus#LOST} if the number of mistakes
     * has reached the maximum allowed. Sets the status to {@link  GameStatus#WON}
     * if the word has been completely guessed. Otherwise, the game remains in progress.h
     */
    private void updateGameStatus() {
        if (mistakeCount >= MAX_MISTAKES) {
            status = GameStatus.LOST;
        } else if(isWordCompletelyGuessed()){
            status = GameStatus.WON;
        }
    }

    /**
     * Checks if all letters in the word have been guessed.
     *
     * @return {@code ture} if every letter in the word has been guessed, {@code false} otherwise
     */
    private boolean isWordCompletelyGuessed() {
        for (int i = 0; i < word.getLength(); i++) {
            Letter wordLetter = new Letter(word.getCharAt(i));
            if (!guessedLetters.contains(wordLetter)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the current state of the word being guessed.
     *
     * Letters that have been guessed correctly are shown, while unguessed letters
     * are represented as underscores ('_')
     *
     * @return a string representing the current state of the word
     */
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

    /**
     * Records a domain event for the current game.
     * @param event
     */
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

    public GamePreferences getPreferences() {
        return preferences;
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

    /**
     * Returns a list of uncommitted domain events for the current game.
     *
     * These events represent actions that have occurred but have not yet been
     * persisted or publisher
     *
     * @return a new {@link List} containing the uncommited {@link GameEvent} instance
     */
    public List<GameEvent> getUncommittedEvents() {
        return new ArrayList<>(events);
    }

    public void clearEvents() {
        events.clear();
    }

    /**
     * Value object representing the result of a letter guess in a Hangman game.
     *
     * Contains information about the current state of the word, remaining tires,
     * game status, and whether the guess was correct or already guessed.
     *
     * Usage
     * <pre>
     *     GuessResult result = game.guessResult(new Letter('a'));
     *     boolean correct = result.isWasCorrect();
     *     int remaining = result.getRemainingTries();
     * </pre>
     */
    public static class GuessResult {

        private final String currentState;
        private final int remainingTries;
        private final GameStatus gameStatus;
        private final boolean wasCorrect;
        private final boolean wasAlreadyGuessed;
        private final Language language;

        /**
         * Creates a new GuessResult.
         *
         * @param currentState the current state of the word (letters guessed and underscores)
         * @param remainingTries the number of remining incorrect guessed allowed
         * @param gameStatus the current status of the game
         * @param wasCorrect {@code ture} if the guessed letter was correct
         * @param wasAlreadyGuessed {@code true} if the letter was had already been guessed
         */
        public GuessResult(
                String currentState, int remainingTries, GameStatus gameStatus,
                boolean wasCorrect, boolean wasAlreadyGuessed, Language language) {
            this.currentState = currentState;
            this.remainingTries = remainingTries;
            this.gameStatus = gameStatus;
            this.wasCorrect = wasCorrect;
            this.wasAlreadyGuessed = wasAlreadyGuessed;
            this.language = language;
        }

        public String getCurrentState () {
            return currentState;
        }

        public int getRemainingTries () {
            return remainingTries;
        }

        public GameStatus getGameStatus () {
            return gameStatus;
        }

        public boolean isWasCorrect () {
            return wasCorrect;
        }

        public boolean isWasAlreadyGuessed () {
            return wasAlreadyGuessed;
        }

        public Language getLanguage() {
            return this.language;
        }
    }

}
