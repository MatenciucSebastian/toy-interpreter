package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class MyHeapTest {
    private IHeap heap;

    @BeforeEach
    void setUp() {
        heap = new MyHeap();
    }

    @Test
    void allocateGivesTheAddressesOneTwoThree() {
        assertEquals(1, heap.allocate(new IntValue(10)));
        assertEquals(2, heap.allocate(new IntValue(20)));
        assertEquals(3, heap.allocate(new IntValue(30)));
    }

    @Test
    void getReturnsTheAllocatedValue() throws ADTException {
        int address = heap.allocate(new IntValue(10));
        assertTrue(heap.isDefined(address));
        assertEquals(new IntValue(10), heap.get(address));
    }

    @Test
    void updateChangesTheValueAtAnAddress() throws ADTException {
        int address = heap.allocate(new IntValue(10));
        heap.update(address, new IntValue(20));
        assertEquals(new IntValue(20), heap.get(address));
    }

    @Test
    void addressZeroIsNeverDefined() {
        heap.allocate(new IntValue(10));
        assertFalse(heap.isDefined(0));
    }

    @Test
    void getOfAMissingAddressThrows() {
        var ex = assertThrows(ADTException.class, () -> heap.get(1));
        assertTrue(ex.getMessage().contains("is not in the heap"));
    }

    @Test
    void updateOfAMissingAddressThrows() {
        var ex = assertThrows(ADTException.class, () -> heap.update(1, new IntValue(5)));
        assertTrue(ex.getMessage().contains("is not in the heap"));
    }

    @Test
    void getContentIsAReadOnlyCopy() {
        heap.allocate(new IntValue(10));
        Map<Integer, IValue> copy = heap.getContent();
        assertThrows(UnsupportedOperationException.class, () -> copy.put(2, new IntValue(20)));
        heap.allocate(new IntValue(20));
        assertEquals(Map.of(1, new IntValue(10)), copy);
    }

    @Test
    void setContentReplacesTheContentButNotTheNextFreeAddress() {
        heap.allocate(new IntValue(10));
        heap.allocate(new IntValue(20));
        heap.setContent(Map.of(2, new IntValue(20)));
        assertEquals(Map.of(2, new IntValue(20)), heap.getContent());
        assertEquals(3, heap.allocate(new IntValue(30)));
    }
}
