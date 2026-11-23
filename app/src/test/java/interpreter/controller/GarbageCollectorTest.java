package interpreter.controller;

import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class GarbageCollectorTest {
    // the state of Ex9 after new(v, 30): v -> (3, int), a -> (2, Ref(int)); 4 is garbage
    private final List<IValue> symTable = List.of(
            new RefValue(3, new IntType()), new RefValue(2, new RefType(new IntType())));
    private final Map<Integer, IValue> heap = Map.of(
            1, new IntValue(20), 2, new RefValue(1, new IntType()),
            3, new IntValue(30), 4, new IntValue(7));

    @Test
    void getAddrFromSymTableKeepsOnlyTheAddressesOfReferences() {
        List<IValue> values = List.of(new RefValue(3, new IntType()), new IntValue(5),
                new BoolValue(true), new RefValue(0, new IntType()));
        assertEquals(Set.of(3, 0), GarbageCollector.getAddrFromSymTable(values));
    }

    @Test
    void unsafeCollectorRemovesALocationReachableOnlyThroughTheHeap() {
        Set<Integer> roots = GarbageCollector.getAddrFromSymTable(symTable);
        Map<Integer, IValue> kept = GarbageCollector.unsafeGarbageCollector(roots, heap);
        assertEquals(Set.of(2, 3), kept.keySet());          // 1 is lost: rH(rH(a)) would fail
    }

    @Test
    void keepsTheLocationsReachableThroughTheHeap() {
        Set<Integer> roots = GarbageCollector.getAddrFromSymTable(symTable);
        Map<Integer, IValue> kept = GarbageCollector.safeGarbageCollector(roots, heap);
        assertEquals(Set.of(1, 2, 3), kept.keySet());       // 4 is garbage, 1 is not
        assertEquals(new IntValue(20), kept.get(1));
    }

    @Test
    void laboratory8Exercise2() {
        // SymTable: a -> (3, Ref(int)), c -> (0, int)
        // Heap: 1 -> 7, 2 -> 8, 3 -> (2, int), 4 -> (1, int), 5 -> (4, Ref(int))
        List<IValue> symTable = List.of(new RefValue(3, new RefType(new IntType())), new RefValue(0, new IntType()));
        Map<Integer, IValue> heap = Map.of(
                1, new IntValue(7), 2, new IntValue(8), 3, new RefValue(2, new IntType()),
                4, new RefValue(1, new IntType()), 5, new RefValue(4, new RefType(new IntType())));
        Set<Integer> roots = GarbageCollector.getAddrFromSymTable(symTable);

        assertEquals(Set.of(2, 3), GarbageCollector.reachableAddresses(roots, heap));
        assertEquals(Set.of(3), GarbageCollector.unsafeGarbageCollector(roots, heap).keySet());
        assertEquals(Set.of(2, 3), GarbageCollector.safeGarbageCollector(roots, heap).keySet());
    }

    @Test
    void reachableAddressesFollowsALongChain() {
        // 4 -> 3 -> 2 -> 1, the SymTable refers only to 4
        Map<Integer, IValue> chain = Map.of(1, new IntValue(1), 2, new RefValue(1, new IntType()),
                3, new RefValue(2, new IntType()), 4, new RefValue(3, new IntType()), 5, new IntValue(5));
        assertEquals(Set.of(1, 2, 3, 4), GarbageCollector.reachableAddresses(Set.of(4), chain));
    }

    @Test
    void reachableAddressesStopsOnACycle() {
        // not possible in a well-typed program, but the collector must still terminate
        Map<Integer, IValue> cycle = Map.of(1, new RefValue(2, new IntType()), 2, new RefValue(1, new IntType()));
        assertEquals(Set.of(1, 2), GarbageCollector.reachableAddresses(Set.of(1), cycle));
    }

    @Test
    void anEmptySymTableMakesTheWholeHeapGarbage() {
        assertTrue(GarbageCollector.safeGarbageCollector(Set.of(), heap).isEmpty());
    }
}
