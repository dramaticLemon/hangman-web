package com.join.tab.application.dto;

import java.util.List;
import java.util.Map;

/**
 * Data transfer Object representing information about language(s) in the system.
 * This DTO can be used in two modes;
 * 1. Single language info - provides details about a specific language,
 * including code, display name, number of categories, word cound, and whether
 * it is supported.
 * 2.Multiple languages info - provides a list of all supported languages and a
 * map of language-specific data.
 */
public class LanguageInfoDto {
    private final String languageCode;
    private final String displayName;
    private final long categories;
    private final long wordCount;
    private final boolean supported;

    // for single language info
    public LanguageInfoDto(
            String languageCode, String displayName, long categories,
            long wordCound, boolean supported) {

       this.languageCode = languageCode;
       this.displayName = displayName;
       this.categories = categories;
       this.wordCount = wordCound;
       this.supported = supported;
       this.allLanguages = null;
       this.languagesData = null;
    }

    // for multiple languages info
    private final List<String> allLanguages;
    private final Map<String, Object> languagesData;

    public LanguageInfoDto(List<String> allLanguages, Map<String,Object> languagesData) {
        this.allLanguages = allLanguages;
        this.languagesData = languagesData;
        this.languageCode = null;
        this.displayName = null;
        this.categories = 0;
        this.wordCount = 0;
        this.supported = false;
    }

    public String getLanguageCode () {
        return languageCode;
    }

    public String getDisplayName () {
        return displayName;
    }

    /**
     * Count categories
     * @return count of categories
     */
    public long getCategories () {
        return categories;
    }

    public long getWordCount() {
        return wordCount;
    }

    public boolean isSupported () {
        return supported;
    }

    public List<String> getAllLanguages () {
        return allLanguages;
    }

    public Map<String, Object> getLanguagesData () {
        return languagesData;
    }
}
