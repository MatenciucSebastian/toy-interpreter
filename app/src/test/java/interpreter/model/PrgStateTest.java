package interpreter.model;

import interpreter.exceptions.MyException;
import interpreter.model.exp.VarExp;
import interpreter.model.stmt.CompStmt;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NopStmt;
import interpreter.model.stmt.PrintStmt;
import interpreter.model.type.IntType;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class PrgStateTest {
    private static final IStmt PROGRAM = new CompStmt(new NopStmt(), new PrintStmt(new VarExp("v")));

    @Test
    void ofPutsTheProgramOnTheStackAndCreatesEmptyStructures() throws MyException {
        PrgState state = PrgState.of(PROGRAM);
        assertEquals(PROGRAM, state.getOriginalProgram());
        assertEquals(PROGRAM, state.getExeStack().pop());
        assertTrue(state.getExeStack().isEmpty());
        assertTrue(state.getSymTable().toMap().isEmpty());
        assertEquals(0, state.getOut().size());
        assertTrue(state.getFileTable().toMap().isEmpty());
        assertTrue(state.getHeap().getContent().isEmpty());
    }

    @Test
    void toLogStringHasTheFiveSectionsWithOneElementPerLine() throws MyException {
        PrgState state = PrgState.of(PROGRAM);
        state.getSymTable().put("v", new IntValue(2));
        state.getOut().add(new IntValue(2));
        state.getFileTable().put(new StringValue("test.in"), new BufferedReader(new StringReader("")));
        state.getHeap().allocate(new IntValue(20));

        String expected = """
                ExeStack:
                (nop; print(v))
                SymTable:
                v --> 2
                Out:
                2
                FileTable:
                test.in
                Heap:
                1 --> 20
                """;
        assertEquals(expected, state.toLogString());
    }

    @Test
    void toLogStringListsTheHeapSortedByAddress() {
        PrgState state = PrgState.of(PROGRAM);
        state.getHeap().setContent(Map.of(17, new IntValue(5), 2, new RefValue(17, new IntType())));
        assertTrue(state.toLogString().endsWith("Heap:\n2 --> (17, int)\n17 --> 5\n"));
    }

    @Test
    void toLogStringListsTheTopOfTheStackFirst() throws MyException {
        PrgState state = PrgState.of(PROGRAM);
        state.getExeStack().pop().execute(state);

        String expected = """
                ExeStack:
                nop
                print(v)
                SymTable:
                Out:
                FileTable:
                Heap:
                """;
        assertEquals(expected, state.toLogString());
    }
}
