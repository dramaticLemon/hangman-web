package com.join.tab.application.service;

import com.join.tab.infra.service.WordLoaderService;

import java.io.InputStream;
import java.util.List;

/**
 * Service interface for managing the words used in the Hangman game.
 * Provides methods to load words from files, reload all words, query word statistics,
 * manage individual words, and check for word existence.
 */
public interface WordManagementService {

    /**
     * Loads words from a file into the system under the specified category.
     *
     * @param filePath the path to the file containing words
     * @param category the category to assign to the words
     * @return a {@link WordLoaderService.WordLoadResult} with the results of the loading process
     */
    WordLoaderService.WordLoadResult loadWordsFromFile(String filePath, String category);

    /** Loads words from an InputStream into a given language and category*/
    WordLoaderService.WordLoadResult loadWordsFromStream(InputStream inputStream, String language, String category);

    /** Reloads all words from predefined files, replacing the current active words. */
    void reloadAllWords();

    /** Returns the total count of active words in the system */
    long getWordCount();

    /** Returns a list of all available word categories */
    List<String> getAvailableCategories();

    /**
     * Adds a new word under the specified category and language.
     *
     * @param word the word to add
     * @param category the category for the word
     * @return {@code true} if the word was added successfully, {@code false} otherwise
     */
    boolean addWord(String word, String language, String category);

    /**
     * Removes a word from the system.
     *
     * @param word the word to remove
     * @return {@code true} if the word was removed successfully, {@code false} if it did not exist
     */
    boolean removeWord(String word, String language);

    /**
     * Checks if a word exists in the system.
     *
     * @param word the word to check
     * @return {@code true} if the word exists, {@code false} otherwise
     */
    boolean wordExists(String word, String language);
}
