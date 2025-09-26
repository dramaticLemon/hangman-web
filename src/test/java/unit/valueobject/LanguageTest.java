package unit.valueobject;

import com.join.tab.domain.valueobject.Language;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LanguageTest {

    @Test
    void constructorShouldSetDefaultWhenCodeIsNullOrEmpty() {
        Language lang1 = new Language(null);
        assertEquals("en", lang1.getCode());

        Language lang2 = new Language("");
        assertEquals("en", lang2.getCode());

        Language lang3 = new Language("   ");
        assertEquals("en", lang3.getCode());
    }

    @Test
    void constructorShouldNormalizeCodeToLowerCaseAndTrim() {
        Language lang = new Language("  DE  ");
        assertEquals("de", lang.getCode());
    }

    @Test
    void constructorShouldThrowExceptionForUnsupportedLanguage() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Language("jp"));
        assertTrue(exception.getMessage().contains("Unsupported language"));
    }

    @Test
    void defaultLanguageShouldReturnEnglish() {
        Language defaultLang = Language.defaultLanguage();
        assertEquals("en", defaultLang.getCode());
    }

    @Test
    void getDisplayNameShouldReturnCorrectNames() {
        assertEquals("English", new Language("en").getDisplayName());
        assertEquals("Українська", new Language("ua").getDisplayName());
        assertEquals("Deutsch", new Language("de").getDisplayName());
        assertEquals("Français", new Language("fr").getDisplayName());
        assertEquals("Español", new Language("es").getDisplayName());
    }

    @Test
    void isSupportedShouldReturnTrueForSupportedLanguages() {
        for (String code : Language.getSupportedLanguage()) {
            assertTrue(new Language(code).isSupported());
        }
    }

    @Test
    void equalsAndHashCodeShouldBeCorrect() {
        Language lang1 = new Language("en");
        Language lang2 = new Language("EN");
        Language lang3 = new Language("fr");

        assertEquals(lang1, lang2);
        assertEquals(lang1.hashCode(), lang2.hashCode());
        assertNotEquals(lang1, lang3);
        assertNotEquals(lang1.hashCode(), lang3.hashCode());
    }

    @Test
    void toStringShouldReturnCode() {
        Language lang = new Language("es");
        assertEquals("es", lang.toString());
    }

    @Test
    void getSupportedLanguageShouldReturnImmutableSet() {
        Set<String> supported = Language.getSupportedLanguage();
        assertTrue(supported.contains("en"));
        assertThrows(UnsupportedOperationException.class, () -> supported.add("jp"));
    }
}
