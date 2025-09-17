package com.join.tab.domain.event;

import com.join.tab.domain.model.valueobject.GameId;
import com.join.tab.domain.model.valueobject.Letter;
import lombok.Getter;

@Getter
public class LetterGuessedEvent extends  GameEvent{
    private final Letter letter;
    private final boolean wasCorrect;

    public LetterGuessedEvent(GameId gameid, Letter letter, boolean wasCorrect) {
        super(gameid);
        this.letter = letter;
        this.wasCorrect = wasCorrect;
    }

}
