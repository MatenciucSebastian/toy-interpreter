package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.adt.*;
import interpreter.model.type.IntType;
import interpreter.model.type.StringType;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VarDeclStmtTest {
    private PrgState state;

    @BeforeEach
    void setUp() {
        state = PrgState.of(new NopStmt());
    }

    @Test
    void validDeclarationAddsToSymTable() throws MyException {
        IStmt stmt = new VarDeclStmt("v", new IntType());
        stmt.execute(state);
        assertTrue(state.getSymTable().isDefined("v"));
    }

    @Test
    void stringVariableGetsTheEmptyStringAsDefaultValue() throws MyException {
        new VarDeclStmt("varf", new StringType()).execute(state);
        assertEquals(new StringValue(""), state.getSymTable().lookup("varf"));
    }

    @Test
    void redeclaredVariableThrows() throws MyException {
        IStmt stmt1 = new VarDeclStmt("v", new IntType());
        IStmt stmt2 = new VarDeclStmt("v", new IntType());
        stmt1.execute(state);

        var ex = assertThrows(StatementException.class, () -> stmt2.execute(state));
        assertTrue(ex.getMessage().contains("already declared"));
    }
}