package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.adt.*;
import interpreter.model.exp.ValueExp;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AssignStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() {
        state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), new NopStmt());
    }

    @Test
    void validAssignmentUpdatesSymTable() throws MyException {
        state.getSymTable().put("v", new IntValue(0));
        IStmt stmt = new AssignStmt("v", new ValueExp(new IntValue(5)));
        stmt.execute(state);
        assertEquals(new IntValue(5), state.getSymTable().lookup("v"));
    }

    @Test
    void undeclaredVariableThrows() {
        IStmt stmt = new AssignStmt("missing", new ValueExp(new IntValue(5)));
        var ex = assertThrows(StatementException.class, () -> stmt.execute(state));
        assertTrue(ex.getMessage().contains("not declared"));
    }

    @Test
    void typeMismatchThrows() throws MyException {
        state.getSymTable().put("v", new IntValue(0));
        IStmt stmt = new AssignStmt("v", new ValueExp(new BoolValue(true)));
        var ex = assertThrows(StatementException.class, () -> stmt.execute(state));
        assertTrue(ex.getMessage().contains("do not match"));
    }
}