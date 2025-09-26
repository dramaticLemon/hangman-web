package unit.model;

import com.join.tab.domain.model.Word;
import com.join.tab.domain.valueobject.Language;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WordTest {

    @Test
    void testWordCreationValidWord() {
        Language en = new Language("en");
        Word word = new Word("hello", en);

        assertEquals("hello", word.getValue());
        assertEquals(en, word.getLanguage());
        assertEquals(5, word.getLength());
    }

    @Test
    void testWordCreationNullValueThrowsException() {
        Language en = new Language("en");
        assertThrows(IllegalArgumentException.class, () -> new Word(null, en));
    }

    @Test
    void testWordCreationEmptyValueThrowsException() {
        Language en = new Language("en");
        assertThrows(IllegalArgumentException.class, () -> new Word(" ", en));
    }

    @Test
    void testWordCreationNullLanguageThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Word("hello", null));
    }

    @Test
    void testGetCharAtValidIndex() {
        Language en = new Language("en");
        Word word = new Word("hello", en);
        assertEquals('h', word.getCharAt(0));
        assertEquals('e', word.getCharAt(1));
    }

    @Test
    void testGetCharAtInvalidIndexThrowsException() {
        Language en = new Language("en");
        Word word = new Word("hello", en);
        assertThrows(IndexOutOfBoundsException.class, () -> word.getCharAt(10));
    }

    @Test
    void testContains() {
        Language en = new Language("en");
        Word word = new Word("hello", en);

        assertTrue(word.contains('h'));
        assertTrue(word.contains('e'));
        assertFalse(word.contains('z'));
    }

    @Test
    void testEqualsAndHashCode() {
        Language en = new Language("en");
        Word word1 = new Word("hello", en);
        Word word2 = new Word("hello", en);
        Word word3 = new Word("world", en);

        assertEquals(word1, word2);
        assertNotEquals(word1, word3);

        assertEquals(word1.hashCode(), word2.hashCode());
        assertNotEquals(word1.hashCode(), word3.hashCode());
    }

    @Test
    void testToString() {
        Language en = new Language("en");
        Word word = new Word("hello", en);

        String expected = "Word{value='hello', language='en'}";
        assertEquals(expected, word.toString());
    }

}
