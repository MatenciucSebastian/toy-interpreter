package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HeapStmtTest {
    private PrgState state;

    private static ValueExp num(int n) { return new ValueExp(new IntValue(n)); }

    @BeforeEach
    void setUp() throws MyException {
        state = PrgState.of(new NopStmt());
        new VarDeclStmt("v", new RefType(new IntType())).execute(state);    // Ref int v
    }

    // new (S11)

    @Test
    void newAllocatesAtAddressOneAndUpdatesTheVariable() throws MyException {
        new NewStmt("v", num(20)).execute(state);
        assertEquals(new RefValue(1, new IntType()), state.getSymTable().lookup("v"));
        assertEquals(new IntValue(20), state.getHeap().get(1));
    }

    @Test
    void newOnTheSameVariableTwiceAllocatesANewAddress() throws MyException {
        new NewStmt("v", num(20)).execute(state);
        new NewStmt("v", num(30)).execute(state);
        assertEquals(new RefValue(2, new IntType()), state.getSymTable().lookup("v"));
        assertEquals(new IntValue(20), state.getHeap().get(1));
        assertEquals(new IntValue(30), state.getHeap().get(2));
    }

    @Test
    void newStoresAReferenceForARefRefVariable() throws MyException {
        new NewStmt("v", num(20)).execute(state);
        new VarDeclStmt("a", new RefType(new RefType(new IntType()))).execute(state);
        new NewStmt("a", new VarExp("v")).execute(state);
        assertEquals(new RefValue(2, new RefType(new IntType())), state.getSymTable().lookup("a"));
        assertEquals(new RefValue(1, new IntType()), state.getHeap().get(2));
    }

    @Test
    void newOnAnUndeclaredVariableThrows() {
        var ex = assertThrows(StatementException.class, () -> new NewStmt("x", num(1)).execute(state));
        assertTrue(ex.getMessage().contains("is not declared"));
    }

    @Test
    void newOnAVariableThatIsNotAReferenceThrows() throws MyException {
        new VarDeclStmt("x", new IntType()).execute(state);
        var ex = assertThrows(StatementException.class, () -> new NewStmt("x", num(1)).execute(state));
        assertTrue(ex.getMessage().contains("is not a reference"));
    }

    @Test
    void newWithAValueOfTheWrongTypeThrows() {
        var exp = new ValueExp(new BoolValue(true));
        var ex = assertThrows(StatementException.class, () -> new NewStmt("v", exp).execute(state));
        assertTrue(ex.getMessage().contains("expected int"));
        assertTrue(state.getHeap().getContent().isEmpty());
    }

    // wH (S12)

    @Test
    void writeChangesTheHeapLocation() throws MyException {
        new NewStmt("v", num(20)).execute(state);
        new WriteHeapStmt("v", num(30)).execute(state);
        assertEquals(new IntValue(30), state.getHeap().get(1));
        assertEquals(new RefValue(1, new IntType()), state.getSymTable().lookup("v"));
    }

    @Test
    void writeOnAnUndeclaredVariableThrows() {
        var ex = assertThrows(StatementException.class, () -> new WriteHeapStmt("x", num(1)).execute(state));
        assertTrue(ex.getMessage().contains("is not declared"));
    }

    @Test
    void writeOnAVariableThatIsNotAReferenceThrows() throws MyException {
        new VarDeclStmt("x", new IntType()).execute(state);
        var ex = assertThrows(StatementException.class, () -> new WriteHeapStmt("x", num(1)).execute(state));
        assertTrue(ex.getMessage().contains("is not a reference"));
    }

    @Test
    void writeToAnAddressThatIsNotInTheHeapThrows() {
        // v still has its default value (0, int): address 0 is null
        var ex = assertThrows(StatementException.class, () -> new WriteHeapStmt("v", num(1)).execute(state));
        assertTrue(ex.getMessage().contains("address 0 is not in the heap"));
    }

    @Test
    void writeWithAValueOfTheWrongTypeThrows() throws MyException {
        new NewStmt("v", num(20)).execute(state);
        var exp = new ValueExp(new BoolValue(true));
        var ex = assertThrows(StatementException.class, () -> new WriteHeapStmt("v", exp).execute(state));
        assertTrue(ex.getMessage().contains("expected int"));
        assertEquals(new IntValue(20), state.getHeap().get(1));
    }

    @Test
    void toStringGivesTheStatementsInTheirUsualForm() {
        assertEquals("new(v, 20)", new NewStmt("v", num(20)).toString());
        assertEquals("wH(v, 30)", new WriteHeapStmt("v", num(30)).toString());
    }
}
