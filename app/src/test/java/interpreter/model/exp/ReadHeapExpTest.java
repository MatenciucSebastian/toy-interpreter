package interpreter.model.exp;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.IHeap;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyHeap;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReadHeapExpTest {
    private IDictionary<String, IValue> symTable;
    private IHeap heap;

    @BeforeEach
    void setUp() throws MyException {
        symTable = new MyDictionary<>();
        heap = new MyHeap();
        // the state of Ex7 after new(a, v): heap {1 -> 20, 2 -> (1, int)}
        symTable.put("v", new RefValue(heap.allocate(new IntValue(20)), new IntType()));
        symTable.put("a", new RefValue(heap.allocate(new RefValue(1, new IntType())), new RefType(new IntType())));
    }

    @Test
    void readsTheValueAtTheAddress() throws MyException {
        assertEquals(new IntValue(20), new ReadHeapExp(new VarExp("v")).eval(symTable, heap));
    }

    @Test
    void readsThroughTwoReferences() throws MyException {
        IExp e = new ReadHeapExp(new ReadHeapExp(new VarExp("a")));
        assertEquals(new IntValue(20), e.eval(symTable, heap));
    }

    @Test
    void canBeUsedInsideAnArithmeticExpression() throws MyException {
        IExp e = new ArithExp(ArithOp.ADD, new ReadHeapExp(new VarExp("v")), new ValueExp(new IntValue(5)));
        assertEquals(new IntValue(25), e.eval(symTable, heap));
    }

    @Test
    void anExpressionThatIsNotAReferenceThrows() {
        IExp e = new ReadHeapExp(new ValueExp(new IntValue(1)));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable, heap));
        assertTrue(ex.getMessage().contains("is not a reference"));
    }

    @Test
    void anAddressThatIsNotInTheHeapThrows() {
        IExp e = new ReadHeapExp(new ValueExp(new RefValue(7, new IntType())));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable, heap));
        assertEquals("rH: address 7 is not in the heap", ex.getMessage());
    }

    @Test
    void toStringIsRHOfTheExpression() {
        assertEquals("rH(rH(a))", new ReadHeapExp(new ReadHeapExp(new VarExp("a"))).toString());
    }
}
