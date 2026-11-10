package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyListTest {
    private IList<String> list;

    @BeforeEach
    void setUp() {
        list = new MyList<>();
    }

    @Test
    void addGetAndSizeWorkCorrectly() throws ADTException {
        list.add("first");
        list.add("second");

        assertEquals(2, list.size());
        assertEquals("first", list.get(0));
        assertEquals("second", list.get(1));
    }

    @Test
    void getOutOfBoundsThrows() {
        var ex = assertThrows(ADTException.class, () -> list.get(0));
        assertEquals("Index out of bounds", ex.getMessage());
    }

    @Test
    void toListGivesTheElementsInInsertionOrder() {
        list.add("first");
        list.add("second");
        list.add("third");
        assertEquals(List.of("first", "second", "third"), list.toList());
    }

    @Test
    void toListIsAReadOnlyCopy() {
        list.add("first");
        List<String> copy = list.toList();
        assertThrows(UnsupportedOperationException.class, () -> copy.add("second"));
        list.add("second");
        assertEquals(List.of("first"), copy);
    }
}