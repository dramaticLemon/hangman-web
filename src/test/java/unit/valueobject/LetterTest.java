package unit.valueobject;

import com.join.tab.domain.valueobject.Letter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LetterTest {

    @Test
    void constructorShouldStoreLowercaseForLatinLetter() {
        Letter letter = new Letter('A');
        assertEquals('a', letter.getValue());
        assertTrue(letter.isLatinAlphabet());
        assertFalse(letter.isCyrillicAlphabet());
    }

    @Test
    void constructorShouldStoreLowercaseForCyrillicLetter() {
        Letter letter = new Letter('Ж');
        assertEquals('ж', letter.getValue());
        assertFalse(letter.isLatinAlphabet());
        assertTrue(letter.isCyrillicAlphabet());
    }

    @Test
    void constructorShouldThrowExceptionForInvalidLetter() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Letter('1'));
        assertEquals("Must be a valid letter", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () ->
                new Letter('%'));
        assertEquals("Must be a valid letter", exception.getMessage());
    }

    @Test
    void equalsAndHashCodeShouldBeCorrect() {
        Letter l1 = new Letter('a');
        Letter l2 = new Letter('A');
        Letter l3 = new Letter('b');

        assertEquals(l1, l2);
        assertEquals(l1.hashCode(), l2.hashCode());

        assertNotEquals(l1, l3);
        assertNotEquals(l1.hashCode(), l3.hashCode());
    }

    @Test
    void toStringShouldReturnValueAsString() {
        Letter letter = new Letter('B');
        assertEquals("b", letter.toString());
    }
}
