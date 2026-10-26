package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() {
        state = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), new NopStmt());
    }

    @Test
    void executePushesStatementsInReverseOrder() throws MyException {
        IStmt s1 = new NopStmt();
        IStmt s2 = new NopStmt();
        IStmt comp = new CompStmt(s1, s2);

        comp.execute(state);

        assertEquals(s1, state.getExeStack().pop());
        assertEquals(s2, state.getExeStack().pop());
    }
}