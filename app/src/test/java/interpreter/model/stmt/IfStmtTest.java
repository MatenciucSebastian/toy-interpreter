package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.adt.*;
import interpreter.model.exp.ValueExp;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IfStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() {
        state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), new NopStmt());
    }

    @Test
    void trueConditionPushesThenStatement() throws MyException {
        IStmt thenS = new NopStmt();
        IStmt elseS = new NopStmt();
        IStmt stmt = new IfStmt(new ValueExp(new BoolValue(true)), thenS, elseS);

        stmt.execute(state);
        assertEquals(thenS, state.getExeStack().pop());
    }

    @Test
    void nonBooleanConditionThrows() {
        IStmt stmt = new IfStmt(new ValueExp(new IntValue(1)), new NopStmt(), new NopStmt());
        var ex = assertThrows(StatementException.class, () -> stmt.execute(state));
        assertTrue(ex.getMessage().contains("not a boolean"));
    }
}