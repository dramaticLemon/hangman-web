package unit.valueobject;

import com.join.tab.domain.enums.DifficultyLevel;
import com.join.tab.domain.valueobject.GamePreferences;
import com.join.tab.domain.valueobject.Language;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GamePreferencesTest {

    @Test
    void constructorShouldSetDefaultLanguageWhenLanguageIsNull() {
        GamePreferences prefs = new GamePreferences(null);
        assertNotNull(prefs.getLanguage());
        assertEquals("en", prefs.getLanguage().getCode());
        assertNull(prefs.getCategory());
        assertNull(prefs.getDifficulty());
    }

    @Test
    void constructorShouldSetLanguageAndCategory() {
        Language lang = new Language("fr");
        GamePreferences prefs = new GamePreferences(lang, "animals");

        assertEquals(lang, prefs.getLanguage());
        assertEquals("animals", prefs.getCategory());
        assertNull(prefs.getDifficulty());
        assertTrue(prefs.hasCategory());
        assertFalse(prefs.hasDifficulty());
    }

    @Test
    void constructorShouldSetAllFields() {
        Language lang = new Language("de");
        GamePreferences prefs = new GamePreferences(lang, "food", DifficultyLevel.MEDIUM);

        assertEquals(lang, prefs.getLanguage());
        assertEquals("food", prefs.getCategory());
        assertEquals(DifficultyLevel.MEDIUM, prefs.getDifficulty());
        assertTrue(prefs.hasCategory());
        assertTrue(prefs.hasDifficulty());
    }

    @Test
    void defaultPreferencesShouldReturnPreferencesWithDefaultLanguage() {
        GamePreferences prefs = GamePreferences.defaultPreferences();
        assertEquals("en", prefs.getLanguage().getCode());
        assertNull(prefs.getCategory());
        assertNull(prefs.getDifficulty());
    }

    @Test
    void withLanguageShouldReturnPreferencesWithGivenLanguage() {
        Language lang = new Language("ua");
        GamePreferences prefs = GamePreferences.withLanguage(lang);
        assertEquals(lang, prefs.getLanguage());
        assertNull(prefs.getCategory());
        assertNull(prefs.getDifficulty());
    }

    @Test
    void equalsAndHashCodeShouldWorkCorrectly() {
        GamePreferences p1 = new GamePreferences(new Language("en"), "animals", DifficultyLevel.EASY);
        GamePreferences p2 = new GamePreferences(new Language("en"), "animals", DifficultyLevel.EASY);
        GamePreferences p3 = new GamePreferences(new Language("fr"), "animals", DifficultyLevel.EASY);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1, p3);
        assertNotEquals(p1.hashCode(), p3.hashCode());
    }

    @Test
    void hasCategoryShouldReturnFalseWhenCategoryIsNullOrEmpty() {
        GamePreferences p1 = new GamePreferences(new Language("en"), null);
        GamePreferences p2 = new GamePreferences(new Language("en"), "   ");

        assertFalse(p1.hasCategory());
        assertFalse(p2.hasCategory());
    }

    @Test
    void hasDifficultyShouldReturnFalseWhenDifficultyIsNull() {
        GamePreferences prefs = new GamePreferences(new Language("en"), "any", null);
        assertFalse(prefs.hasDifficulty());
    }

}
