package com.join.tab.application.service;

import com.join.tab.application.dto.GameDto;
import com.join.tab.application.dto.GuessDto;

public interface HangmanGameService {
    GameDto startNewGame(String sessionId);
    GuessDto guessLetter(String sessionId, char letter);
    GameDto getCurrentGame(String sessionId);
    void endGame(String sessionId);


}
