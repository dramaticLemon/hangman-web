package com.join.tab.application.service;

import com.join.tab.infra.service.WordLoadersService;

import java.util.List;

public interface WordManagementService {
    WordLoadersService.WordLoadResult loadWordsFromFile(String filePath, String category);
    void reloadAllWords();
    long getWordCount();
    List<String> getAvailableCategories();
    boolean addWord(String word, String category);
    boolean removeWord(String word);
    boolean wordExists(String word);
}
