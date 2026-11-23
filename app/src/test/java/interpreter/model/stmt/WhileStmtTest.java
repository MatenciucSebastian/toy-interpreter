package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ArithExp;
import interpreter.model.exp.ArithOp;
import interpreter.model.exp.RelOp;
import interpreter.model.exp.RelationalExp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.IntType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class WhileStmtTest {
    private static ValueExp num(int n) { return new ValueExp(new IntValue(n)); }

    // (while (v > 0) (print(v); v = v - 1))
    private static final IStmt COUNTDOWN = new WhileStmt(
            new RelationalExp(RelOp.GT, new VarExp("v"), num(0)),
            new CompStmt(new PrintStmt(new VarExp("v")),
                    new AssignStmt("v", new ArithExp(ArithOp.SUB, new VarExp("v"), num(1)))));

    /** int v; v = start; COUNTDOWN - executed statement by statement, without a controller. */
    private static List<IValue> outOfCountdownFrom(int start) throws MyException {
        PrgState state = PrgState.of(new CompStmt(new VarDeclStmt("v", new IntType()),
                new CompStmt(new AssignStmt("v", num(start)), COUNTDOWN)));
        while (!state.getExeStack().isEmpty())
            state.getExeStack().pop().execute(state);
        return state.getOut().toList();
    }

    @Test
    void aFalseConditionRunsTheBodyZeroTimes() throws MyException {
        assertEquals(List.of(), outOfCountdownFrom(0));
    }

    @Test
    void theBodyRunsOnce() throws MyException {
        assertEquals(List.of(new IntValue(1)), outOfCountdownFrom(1));
    }

    @Test
    void theBodyRunsSeveralTimes() throws MyException {
        assertEquals(List.of(new IntValue(4), new IntValue(3), new IntValue(2), new IntValue(1)),
                outOfCountdownFrom(4));
    }

    @Test
    void aTrueConditionPushesTheBodyOnTopOfTheLoop() throws MyException {
        PrgState state = PrgState.of(new NopStmt());
        state.getExeStack().pop();
        IStmt body = new PrintStmt(num(1));
        IStmt loop = new WhileStmt(new ValueExp(new BoolValue(true)), body);

        loop.execute(state);

        assertEquals(body, state.getExeStack().pop());
        assertEquals(loop, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void whileWithANonBooleanConditionThrows() {
        PrgState state = PrgState.of(new NopStmt());
        var loop = new WhileStmt(num(1), new NopStmt());
        var ex = assertThrows(StatementException.class, () -> loop.execute(state));
        assertTrue(ex.getMessage().contains("is not a boolean"));
    }

    @Test
    void toStringShowsTheConditionAndTheBody() {
        assertEquals("(while ((v > 0)) (print(v); v=(v - 1)))", COUNTDOWN.toString());
    }
}
