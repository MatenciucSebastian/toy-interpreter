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

class ArithExpTest {
    private IDictionary<String, IValue> symTable;

    private static IExp num(int n) { return new ValueExp(new IntValue(n)); }

    @BeforeEach
    void setUp() {
        symTable = new MyDictionary<>();
    }

    @Test
    void respectsOperatorPrecedenceEncodedInTheTree() throws MyException {
        IExp e = new ArithExp(ArithOp.ADD, num(2), new ArithExp(ArithOp.MUL, num(3), num(5)));
        assertEquals(new IntValue(17), e.eval(symTable));
    }

    @Test
    void subtractionAndDivisionWorkCorrectly() throws MyException {
        IExp e = new ArithExp(ArithOp.SUB, num(10), new ArithExp(ArithOp.DIV, num(8), num(2)));
        assertEquals(new IntValue(6), e.eval(symTable));
    }

    @Test
    void divisionByZeroThrows() {
        IExp e = new ArithExp(ArithOp.DIV, num(1), num(0));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable));
        assertEquals("division by zero", ex.getMessage());
    }

    @Test
    void nonIntegerOperandThrows() {
        IExp e = new ArithExp(ArithOp.ADD, num(1), new ValueExp(new BoolValue(true)));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable));
        assertTrue(ex.getMessage().contains("Second operand not an integer"));
    }
}