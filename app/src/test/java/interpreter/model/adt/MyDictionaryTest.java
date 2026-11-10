package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MyDictionaryTest {
    private IDictionary<String, Integer> dictionary;

    @BeforeEach
    void setUp() {
        dictionary = new MyDictionary<>();
    }

    @Test
    void putAndLookupWorkCorrectly() throws ADTException {
        dictionary.put("a", 1);
        assertTrue(dictionary.isDefined("a"));
        assertEquals(1, dictionary.lookup("a"));
    }

    @Test
    void updateChangesExistingValue() throws ADTException {
        dictionary.put("a", 1);
        dictionary.update("a", 2);
        assertEquals(2, dictionary.lookup("a"));
    }

    @Test
    void lookupOnMissingKeyThrows() {
        var ex = assertThrows(ADTException.class, () -> dictionary.lookup("missing"));
        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void removeDeletesTheKey() throws ADTException {
        dictionary.put("a", 1);
        dictionary.put("b", 2);
        dictionary.remove("a");
        assertFalse(dictionary.isDefined("a"));
        assertTrue(dictionary.isDefined("b"));
    }

    @Test
    void removeOnMissingKeyThrows() {
        var ex = assertThrows(ADTException.class, () -> dictionary.remove("missing"));
        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void toMapContainsAllTheEntries() throws ADTException {
        dictionary.put("b", 2);
        dictionary.put("a", 1);
        dictionary.put("c", 3);
        assertEquals(Map.of("a", 1, "b", 2, "c", 3), dictionary.toMap());
    }

    @Test
    void toMapIsAReadOnlyCopy() throws ADTException {
        dictionary.put("a", 1);
        Map<String, Integer> copy = dictionary.toMap();
        assertThrows(UnsupportedOperationException.class, () -> copy.put("b", 2));
        dictionary.put("c", 3);
        assertEquals(Map.of("a", 1), copy);
    }
}