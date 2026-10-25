package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
}