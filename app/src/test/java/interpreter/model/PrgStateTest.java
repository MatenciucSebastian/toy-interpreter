package interpreter.model;

import interpreter.exceptions.MyException;
import interpreter.model.exp.VarExp;
import interpreter.model.stmt.CompStmt;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NopStmt;
import interpreter.model.stmt.PrintStmt;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.StringReader;
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
    }

    @Test
    void toLogStringHasTheFourSectionsWithOneElementPerLine() throws MyException {
        PrgState state = PrgState.of(PROGRAM);
        state.getSymTable().put("v", new IntValue(2));
        state.getOut().add(new IntValue(2));
        state.getFileTable().put(new StringValue("test.in"), new BufferedReader(new StringReader("")));

        String expected = """
                ExeStack:
                (nop; print(v))
                SymTable:
                v --> 2
                Out:
                2
                FileTable:
                test.in
                """;
        assertEquals(expected, state.toLogString());
    }

    @Test
    void toLogStringListsTheTopOfTheStackFirst() throws MyException {
        PrgState state = PrgState.of(PROGRAM);
        state.getExeStack().pop().execute(state);   // the CompStmt pushes print(v), then nop

        String expected = """
                ExeStack:
                nop
                print(v)
                SymTable:
                Out:
                FileTable:
                """;
        assertEquals(expected, state.toLogString());
    }
}
