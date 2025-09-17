package com.join.tab.domain.service;

import com.join.tab.domain.aggregate.HangmanGame;
import com.join.tab.domain.model.GameId;
import com.join.tab.domain.model.Word;
import com.join.tab.infra.repository.db.WordRepository;
import com.join.tab.infra.persistence.WordJpaEntity;


public class GameFactory {
    private final WordRepository wordRepository;

    public GameFactory(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    public HangmanGame createNewGame(GameId gameId) {
        WordJpaEntity randomWord = wordRepository.findRandom();
        return new HangmanGame(gameId, randomWord);
    }

    public HangmanGame createGameWithWord(GameId gameId, String wordValue) throws IllegalAccessException {
        Word word = new Word(wordValue);
        return new HangmanGame(gameId, word);
    }
}
