package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class FileStmtTest {
    @TempDir
    Path dir;

    private PrgState state;
    private StringValue fileName;
    private ValueExp file;

    @BeforeEach
    void setUp() throws Exception {
        file = fileWith("numbers.in", "15\n50\n");
        fileName = (StringValue) file.value();
        state = PrgState.of(new NopStmt());
        new VarDeclStmt("x", new IntType()).execute(state);
    }

    @AfterEach
    void closeTheFilesLeftOpen() throws IOException {
        for (BufferedReader reader : state.getFileTable().toMap().values())
            reader.close();
    }

    private ValueExp fileWith(String name, String content) throws IOException {
        Path f = dir.resolve(name);
        Files.writeString(f, content);
        return new ValueExp(new StringValue(f.toString()));
    }

    private int x() throws MyException {
        return ((IntValue) state.getSymTable().lookup("x")).val();
    }

    @Test
    void openAddsTheFileToTheFileTable() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertTrue(state.getFileTable().isDefined(fileName));
    }

    @Test
    void openWithANonStringExpressionThrows() {
        IStmt open = new OpenRFileStmt(new ValueExp(new IntValue(1)));
        var ex = assertThrows(StatementException.class, () -> open.execute(state));
        assertTrue(ex.getMessage().contains("is not a string"));
    }

    @Test
    void openingTheSameFileTwiceThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        var ex = assertThrows(StatementException.class, () -> new OpenRFileStmt(file).execute(state));
        assertTrue(ex.getMessage().contains("already open"));
    }

    @Test
    void openingAMissingFileThrows() {
        IStmt open = new OpenRFileStmt(new ValueExp(new StringValue(dir.resolve("missing.in").toString())));
        var ex = assertThrows(StatementException.class, () -> open.execute(state));
        assertTrue(ex.getMessage().contains("cannot open"));
        assertTrue(state.getFileTable().toMap().isEmpty());
    }

    @Test
    void readGivesTheNumbersInOrderThenZeroAtTheEndOfTheFile() throws MyException {
        new OpenRFileStmt(file).execute(state);
        IStmt read = new ReadFileStmt(file, "x");
        read.execute(state);
        assertEquals(15, x());
        read.execute(state);
        assertEquals(50, x());
        read.execute(state);
        assertEquals(0, x());
    }

    @Test
    void readIgnoresTheSpacesAroundTheNumber() throws Exception {
        ValueExp spaces = fileWith("spaces.in", "  42  \n");
        new OpenRFileStmt(spaces).execute(state);
        new ReadFileStmt(spaces, "x").execute(state);
        assertEquals(42, x());
    }

    @Test
    void readIntoAnUndeclaredVariableThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        var ex = assertThrows(StatementException.class, () -> new ReadFileStmt(file, "y").execute(state));
        assertTrue(ex.getMessage().contains("not declared"));
    }

    @Test
    void readIntoANonIntVariableThrows() throws MyException {
        new VarDeclStmt("b", new BoolType()).execute(state);
        new OpenRFileStmt(file).execute(state);
        var ex = assertThrows(StatementException.class, () -> new ReadFileStmt(file, "b").execute(state));
        assertTrue(ex.getMessage().contains("not of type int"));
    }

    @Test
    void readWithANonStringExpressionThrows() {
        IStmt read = new ReadFileStmt(new ValueExp(new IntValue(1)), "x");
        var ex = assertThrows(StatementException.class, () -> read.execute(state));
        assertTrue(ex.getMessage().contains("is not a string"));
    }

    @Test
    void readFromAFileThatIsNotOpenThrows() {
        var ex = assertThrows(StatementException.class, () -> new ReadFileStmt(file, "x").execute(state));
        assertTrue(ex.getMessage().contains("is not open"));
    }

    @Test
    void readingALineThatIsNotAnIntegerThrows() throws Exception {
        ValueExp text = fileWith("text.in", "abc\n");
        new OpenRFileStmt(text).execute(state);
        var ex = assertThrows(StatementException.class, () -> new ReadFileStmt(text, "x").execute(state));
        assertTrue(ex.getMessage().contains("is not an integer"));
    }

    @Test
    void closeRemovesTheFileFromTheFileTable() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new CloseRFileStmt(file).execute(state);
        assertFalse(state.getFileTable().isDefined(fileName));
    }

    @Test
    void closingAFileThatIsNotOpenThrows() {
        var ex = assertThrows(StatementException.class, () -> new CloseRFileStmt(file).execute(state));
        assertTrue(ex.getMessage().contains("is not open"));
    }

    @Test
    void closeWithANonStringExpressionThrows() {
        IStmt close = new CloseRFileStmt(new ValueExp(new IntValue(1)));
        var ex = assertThrows(StatementException.class, () -> close.execute(state));
        assertTrue(ex.getMessage().contains("is not a string"));
    }

    @Test
    void aClosedFileCannotBeReadAndIsReadFromTheStartWhenOpenedAgain() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new ReadFileStmt(file, "x").execute(state);
        new CloseRFileStmt(file).execute(state);
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "x").execute(state));

        new OpenRFileStmt(file).execute(state);
        new ReadFileStmt(file, "x").execute(state);
        assertEquals(15, x());
    }

    @Test
    void toStringGivesTheStatementsInTheirUsualForm() {
        var varf = new VarExp("varf");
        assertEquals("openRFile(varf)", new OpenRFileStmt(varf).toString());
        assertEquals("readFile(varf, varc)", new ReadFileStmt(varf, "varc").toString());
        assertEquals("closeRFile(varf)", new CloseRFileStmt(varf).toString());
    }
}
