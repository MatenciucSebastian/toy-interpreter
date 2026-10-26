package interpreter.model.exp;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VarExpTest {
    private IDictionary<String, IValue> symTable;

    @BeforeEach
    void setUp() {
        symTable = new MyDictionary<>();
    }

    @Test
    void retrievesDefinedVariable() throws MyException {
        symTable.put("x", new IntValue(5));
        IExp e = new VarExp("x");
        assertEquals(new IntValue(5), e.eval(symTable));
    }

    @Test
    void undefinedVariableThrows() {
        IExp e = new VarExp("missingVar");
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable));
        assertTrue(ex.getMessage().contains("not defined"));
    }
}