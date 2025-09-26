package unit.valueobject;

import com.join.tab.domain.valueobject.GameId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameIdTest {

    @Test
    void constructorShouldCreateGameIdWhenValueIsValid() {
        final String session = "testSession";
        GameId gameId = new GameId(session);
        assertNotNull(gameId);
        assertEquals(session, gameId.getValue());
    }

    @Test
    void constructorShouldThrowExceptionWhenValueInNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new GameId(null));
        assertEquals("Game id cannot be null or empty", exception.getMessage());
    }

    @Test
    void constructorShouldThrowExceptionWhenValueIsEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new GameId(""));
        assertEquals("Game id cannot be null or empty", exception.getMessage());
    }

    @Test
    void constructorShouldThrowExceptionWhenValueIsWhitespace() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new GameId("   "));
        assertEquals("Game id cannot be null or empty", exception.getMessage());
    }

    @Test
    void equalsShouldReturnTrueForSameValue() {
        GameId id1 = new GameId("session123");
        GameId id2 = new GameId("session123");
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentValue() {
        GameId id1 = new GameId("session123");
        GameId id2 = new GameId("session456");
        assertNotEquals(id1, id2);
    }

    @Test
    void equalsShouldReturnFalseWhenComparedWithNullOrOtherClass() {
        GameId id = new GameId("session123");
        assertNotEquals(id, null);
        assertNotEquals(id, "some string");
    }
}
