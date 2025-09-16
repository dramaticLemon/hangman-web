package com.join.tab.services;

import org.springframework.stereotype.Service;
import com.join.tab.repository.WordRepository;

@Service
public class WordService {

    private WordRepository repository;

    public WordService(WordRepository repository) {
        this.repository = repository;
    }
    public String getWord() {
       return "test";
    }

}
