package interpreter.model.exp;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LogicExpTest {
    private IDictionary<String, IValue> symTable;

    private static IExp boolExp(boolean b) { return new ValueExp(new BoolValue(b)); }

    @BeforeEach
    void setUp() {
        symTable = new MyDictionary<>();
    }

    @Test
    void logicalAndOrWorkCorrectly() throws MyException {
        IExp andExp = new LogicExp(LogicOp.AND, boolExp(true), boolExp(false));
        assertEquals(new BoolValue(false), andExp.eval(symTable));

        IExp orExp = new LogicExp(LogicOp.OR, boolExp(true), boolExp(false));
        assertEquals(new BoolValue(true), orExp.eval(symTable));
    }

    @Test
    void nonBooleanOperandThrows() {
        IExp e = new LogicExp(LogicOp.AND, boolExp(true), new ValueExp(new IntValue(1)));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable));
        assertTrue(ex.getMessage().contains("Second operand not a boolean"));
    }
}