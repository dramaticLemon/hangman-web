package com.join.tab.domain.service;

import com.join.tab.domain.aggregate.HangmanGame;
import com.join.tab.domain.model.valueobject.GameId;
import com.join.tab.domain.model.Word;
import com.join.tab.domain.repository.WordRepository;

public class GameFactory {
    private final WordRepository wordRepository;

    public GameFactory(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    public HangmanGame createNewGame(GameId gameId) {
        Word randomWord = wordRepository.getRandomWord();
        return new HangmanGame(gameId, randomWord);
    }

    public HangmanGame createGameWithWord(GameId gameId, String wordValue){
        Word word = new Word(wordValue);
        return new HangmanGame(gameId, word);
    }
}
