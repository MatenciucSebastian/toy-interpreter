package interpreter.view;

import interpreter.controller.Controller;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.stmt.IStmt;
import interpreter.model.type.IType;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import interpreter.repository.Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;


class ExamplesTest {
    @TempDir
    Path dir;

    private PrgState run(IStmt program) throws MyException {
        PrgState state = PrgState.of(program);
        var repo = new Repository(state, dir.resolve("log.txt").toString());
        new Controller(repo, false).allStep();
        return state;
    }

    @Test
    void fileExampleReadsBothNumbersAndClosesTheFile() throws Exception {
        Path in = dir.resolve("test.in");
        Files.writeString(in, "15\n50\n");
        PrgState s = run(Examples.fileExample(in.toString()));
        assertEquals(2, s.getOut().size());
        assertEquals(new IntValue(15), s.getOut().get(0));
        assertEquals(new IntValue(50), s.getOut().get(1));
        assertTrue(s.getFileTable().toMap().isEmpty());
    }

    @Test
    void fileExampleWithAnEmptyFilePrintsZeroTwice() throws Exception {
        Path in = dir.resolve("empty.in");
        Files.writeString(in, "");
        PrgState s = run(Examples.fileExample(in.toString()));
        assertEquals(2, s.getOut().size());
        assertEquals(new IntValue(0), s.getOut().get(0));
        assertEquals(new IntValue(0), s.getOut().get(1));
        assertTrue(s.getFileTable().toMap().isEmpty());
    }

    @Test
    void fileExampleWithAMissingFileThrows() {
        IStmt program = Examples.fileExample(dir.resolve("missing.in").toString());
        var ex = assertThrows(StatementException.class, () -> run(program));
        assertTrue(ex.getMessage().contains("cannot open"));
    }

    @Test
    void example4IsTheFileExampleOnTestIn() {
        assertEquals(Examples.fileExample("test.in"), Examples.EX4);
    }

    @Test
    void example5PrintsFive() throws MyException {
        PrgState s = run(Examples.EX5);
        assertTrue(s.getExeStack().isEmpty());
        assertEquals(1, s.getOut().size());
        assertEquals(new IntValue(5), s.getOut().get(0));
    }

    private static final IType INT = new IntType();
    private static final IType REF_INT = new RefType(INT);

    @Test
    void example6PrintsTheTwoReferences() throws MyException {
        PrgState s = run(Examples.EX6);
        assertEquals(Map.of(1, new IntValue(20), 2, new RefValue(1, INT)), s.getHeap().getContent());
        assertEquals(Map.of("v", new RefValue(1, INT), "a", new RefValue(2, REF_INT)), s.getSymTable().toMap());
        assertEquals(List.of(new RefValue(1, INT), new RefValue(2, REF_INT)), s.getOut().toList());
    }

    @Test
    void example7PrintsTwentyAndTwentyFive() throws MyException {
        PrgState s = run(Examples.EX7);
        assertEquals(Map.of(1, new IntValue(20), 2, new RefValue(1, INT)), s.getHeap().getContent());
        assertEquals(Map.of("v", new RefValue(1, INT), "a", new RefValue(2, REF_INT)), s.getSymTable().toMap());
        assertEquals(List.of(new IntValue(20), new IntValue(25)), s.getOut().toList());
    }

    @Test
    void example8PrintsTwentyAndThirtyFive() throws MyException {
        PrgState s = run(Examples.EX8);
        assertEquals(Map.of(1, new IntValue(30)), s.getHeap().getContent());
        assertEquals(Map.of("v", new RefValue(1, INT)), s.getSymTable().toMap());
        assertEquals(List.of(new IntValue(20), new IntValue(35)), s.getOut().toList());
    }

    @Test
    void example9PrintsTwentyWithTheSafeGarbageCollector() throws MyException {
        PrgState s = run(Examples.EX9);
        assertEquals(List.of(new IntValue(20)), s.getOut().toList());
        // location 1 is reachable only through location 2, and it is still there
        assertEquals(Map.of(1, new IntValue(20), 2, new RefValue(1, INT), 3, new IntValue(30)),
                s.getHeap().getContent());
    }

    @Test
    void example10CountsDownFromFourToZero() throws MyException {
        PrgState s = run(Examples.EX10);
        assertEquals(List.of(new IntValue(4), new IntValue(3), new IntValue(2), new IntValue(1), new IntValue(0)),
                s.getOut().toList());
    }
}
