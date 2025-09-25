package com.join.tab.application.service.impl;

import com.join.tab.application.dto.LanguageInfoDto;
import com.join.tab.domain.aggregate.HangmanGame;
import com.join.tab.domain.exception.GameNotFoundException;
import com.join.tab.domain.exception.UnsupportedLanguageException;
import com.join.tab.domain.model.valueobject.GameId;
import com.join.tab.domain.model.valueobject.GamePreferences;
import com.join.tab.domain.model.valueobject.Language;
import com.join.tab.domain.model.valueobject.Letter;
import com.join.tab.domain.repository.GameRepository;
import com.join.tab.domain.service.GameFactory;
import com.join.tab.application.dto.GameDto;
import com.join.tab.application.dto.GuessDto;
import com.join.tab.application.service.HangmanGameService;
import com.join.tab.infra.entity.WordEntity;
import com.join.tab.infra.repository.jpa.impl.JpaWordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class HangmanGameServiceImpl implements HangmanGameService {
    private final static Logger log = LoggerFactory.getLogger(HangmanGameServiceImpl.class);

    private final GameRepository gameRepository;
    private final GameFactory gameFactory;
    private final JpaWordRepository wordRepository;

    public HangmanGameServiceImpl (GameRepository gameRepository, GameFactory gameFactory, JpaWordRepository wordRepository) {
        this.gameRepository = gameRepository;
        this.gameFactory = gameFactory;
        this.wordRepository = wordRepository;
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
        return startNewGameWithLanguage(sessionId, "en");
    }

    /**
     * Starts a new Hangman game for the given session with the specific language.
     * This method performs th following steps:
     * 1. Creates a {@link GameID} based on the provided session ID.
     * 2. Validate the {@code languageCode} and construct a {@link Language} object.
     * 3. Dektes any existing game accosiated with the session to start fresh.
     * 4. Saves the new game to the {@link GameRepository}. 
     * 5. Logs the creation and returns a {@link GameDto} representing the new game.
     * 
     * @param sessionId the unique identifier of the user's sessin
     * @param languageCode the ISo code of the language to use for the gam
     * @return a {@link GamgeDto} representing the new game
     * @throws UnsupportedLanguageException if the provided {@code languageCode} is invalid or not supported
     */
    @Override
    public GameDto startNewGameWithLanguage(String sessionId, String languageCode) {
        try {
            GameId gameId = new GameId(sessionId);
            Language language = new Language(languageCode);

            // remove existing game if any
            gameRepository.delete(gameId);
            HangmanGame game = gameFactory.createNewGameWithLanguage(gameId, language);
            gameRepository.save(game);
            log.info("Started new game for session {} with language {}", sessionId, languageCode);
            return GameDto.fromDomain(game);

        } catch (IllegalArgumentException e) {
            log.error("Invalid language code: {}", languageCode, e);
            throw new UnsupportedLanguageException(languageCode);
        }
    }

    @Override
    public GameDto startNewGameWithPreferences(String sessionId, String languageCode, String category, String difficulty) {
        try {
            GameId gameId = new GameId(sessionId);
            Language language  = new Language(languageCode);

            WordEntity.DifficultyLevel difficultyLevel = null;

            if (difficulty != null && !difficulty.trim().isEmpty()) {
                difficultyLevel = WordEntity.DifficultyLevel.valueOf(difficulty.toUpperCase());
            }

            GamePreferences preferences = new GamePreferences(language, category, difficultyLevel);
            // remove existing game if any
            gameRepository.delete(gameId);
            HangmanGame game = gameFactory.createNewGameWithPreferences(gameId, preferences);
            gameRepository.save(game);

            log.info("Started new game for session {} with preferences: language={}, category={}, difficulty={}",
                    sessionId, languageCode, category, difficultyLevel);

            return GameDto.fromDomain(game);
        } catch (IllegalArgumentException e) {
            log.error("Invalid game preferences: language={}, category={}, difficulty={}",
                    languageCode, category, difficulty, e);
            throw new UnsupportedLanguageException(languageCode);
        }
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

        try {
            Letter domainLetter = new Letter(letter);
            HangmanGame.GuessResult result = game.guessResult(domainLetter);

            gameRepository.save(game);
            log.debug("Letter '{}' guessed for session {}, correct: {}",
                    letter, sessionId, result.isWasCorrect());
            return GuessDto.fromDomain(game, result);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid letter '{}' form game language '{}' in session {}",
                    letter, game.getPreferences().getLanguage().getCode(), sessionId);
            throw  e;
        }
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

    /**
     * Gets detailed inf. about a single lang.
     * This method the following:
     * - Creates a {@link Language} objet from the given language code
     * - Retrieves the number of categories available for this language form the
     *   repository
     * - Retrieves the total number of words available for this language.
     * - Builds and returns a {@link LanguageInfoDto} containing the lang code,
     *   display name, category count, word count, and whether the language is
     *   supported
     *
     * @param languageCode the code of the language to retrieve information for
     *                     (e.g., "en", "ua", "fr")
     * @return a {@link LanguageInfoDto} with details about the language
     * @throws UnsupportedLanguageException if the language code is invalid or not
     * supported
     */
    @Override
    public LanguageInfoDto getLanguageInfo(String languageCode) {
        try {
            Language language = new Language(languageCode);
            long categories = wordRepository.getCategoriesByLanguage(language);
            long wordCount = wordRepository.getWordCountByLanguage(language);

            return new LanguageInfoDto(
                    language.getCode(),
                    language.getDisplayName(),
                    categories,
                    wordCount,
                    language.isSupported()
            );

        } catch (IllegalArgumentException e) {
            throw new UnsupportedLanguageException(languageCode);
        }
    }

    /**
     * Gets information about all supported lang in the game.
     * This method does the following:
     * Fetches all supported language codes form the repository.
     * For each lang, builds a data map with:
     * -the display name of the language
     * -the list of available categories for that lang
     * -a sample word(currently labeled as "wordCound")
     *
     * @return the data in a {@link LanguageInfoDto} object
     */
    @Override
    public LanguageInfoDto getAllLanguagesInfo() {
        List<String> supportedLanguages = wordRepository.getSupportedLanguages();
        Map<String, Object> languagesData = supportedLanguages.stream()
                .collect(Collectors.toMap(
                        langCode -> langCode,
                        langCode -> {
                            Language lang = new Language(langCode);
                            return Map.of(
                                    "displayName", lang.getDisplayName(),
                                    "categories", wordRepository.getCategoriesByLanguage(lang),
                                    "wordCound", wordRepository.getRandomWordByLanguage(lang)
                            );
                        }
                ));

        return new LanguageInfoDto(supportedLanguages, languagesData);
    }
}
