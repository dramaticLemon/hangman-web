package com.join.tab.domain.repository;

import com.join.tab.domain.model.Word;

public interface WordRepository {
    Word getRandomWord() throws IllegalAccessException;
    Word findById(Long id);

}
