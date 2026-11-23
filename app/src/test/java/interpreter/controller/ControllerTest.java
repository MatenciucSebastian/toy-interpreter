package interpreter.controller;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.stmt.CompStmt;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NewStmt;
import interpreter.model.stmt.NopStmt;
import interpreter.model.stmt.VarDeclStmt;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.IntValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {
    @TempDir
    Path dir;

    private PrgState run(IStmt program) throws MyException {
        PrgState state = PrgState.of(program);
        var repo = new Repository(state, dir.resolve("log.txt").toString());
        new Controller(repo, false).allStep();
        return state;
    }

    @Test
    void example1PrintsTwo() throws MyException {
        PrgState state = run(Examples.EX1);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(new IntValue(2), state.getOut().get(0));
    }

    @Test
    void example2PrintsEighteen() throws MyException {
        PrgState state = run(Examples.EX2);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(new IntValue(18), state.getOut().get(0));
    }

    @Test
    void example3PrintsTwo() throws MyException {
        PrgState state = run(Examples.EX3);
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(new IntValue(2), state.getOut().get(0));
    }

    @Test
    void oneStepOnEmptyStackThrows() throws MyException {
        PrgState state = PrgState.of(new NopStmt());
        state.getExeStack().pop();

        Controller controller = new Controller(new Repository(state, dir.resolve("log.txt").toString()), false);

        var ex = assertThrows(StatementException.class, () -> controller.oneStep(state));
        assertTrue(ex.getMessage().contains("stack is empty"));
    }

    @Test
    void logFileContainsTheFiveSectionsInOrder() throws Exception {
        run(Examples.EX1);
        String log = Files.readString(dir.resolve("log.txt"));
        int a = log.indexOf("ExeStack:"), b = log.indexOf("SymTable:"),
                c = log.indexOf("Out:"), d = log.indexOf("FileTable:"), e = log.indexOf("Heap:");
        assertTrue(0 <= a && a < b && b < c && c < d && d < e);
    }

    @Test
    void logFileHasTheInitialStateAndTwoStatesAfterEveryStep() throws Exception {
        run(Examples.EX1);   // 5 steps, each logged before and after the garbage collection
        String log = Files.readString(dir.resolve("log.txt"));
        assertEquals(1 + 2 * 5, log.lines().filter("ExeStack:"::equals).count());
    }

    @Test
    void theGarbageCollectorRunsAfterEveryStep() throws MyException {
        // Ref int v; new(v, 20); new(v, 30) - location 1 is garbage after the second new
        IStmt program = new CompStmt(new VarDeclStmt("v", new RefType(new IntType())),
                new CompStmt(new NewStmt("v", new ValueExp(new IntValue(20))),
                        new NewStmt("v", new ValueExp(new IntValue(30)))));
        PrgState state = run(program);
        assertEquals(Map.of(2, new IntValue(30)), state.getHeap().getContent());
    }
}
