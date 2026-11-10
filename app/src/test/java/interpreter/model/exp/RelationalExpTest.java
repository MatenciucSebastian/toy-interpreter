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

class RelationalExpTest {
    private static final BoolValue TRUE = new BoolValue(true);
    private static final BoolValue FALSE = new BoolValue(false);

    private IDictionary<String, IValue> symTable;

    private static IExp num(int n) { return new ValueExp(new IntValue(n)); }

    private IValue eval(RelOp op, int left, int right) throws MyException {
        return new RelationalExp(op, num(left), num(right)).eval(symTable);
    }

    @BeforeEach
    void setUp() {
        symTable = new MyDictionary<>();
    }

    @Test
    void lessThanGivesTrueAndFalse() throws MyException {
        assertEquals(TRUE, eval(RelOp.LT, 1, 2));
        assertEquals(FALSE, eval(RelOp.LT, 2, 2));
    }

    @Test
    void lessOrEqualGivesTrueAndFalse() throws MyException {
        assertEquals(TRUE, eval(RelOp.LE, 2, 2));
        assertEquals(FALSE, eval(RelOp.LE, 3, 2));
    }

    @Test
    void equalGivesTrueAndFalse() throws MyException {
        assertEquals(TRUE, eval(RelOp.EQ, 2, 2));
        assertEquals(FALSE, eval(RelOp.EQ, 2, 3));
    }

    @Test
    void notEqualGivesTrueAndFalse() throws MyException {
        assertEquals(TRUE, eval(RelOp.NE, 2, 3));
        assertEquals(FALSE, eval(RelOp.NE, 2, 2));
    }

    @Test
    void greaterThanGivesTrueAndFalse() throws MyException {
        assertEquals(TRUE, eval(RelOp.GT, 3, 2));
        assertEquals(FALSE, eval(RelOp.GT, 2, 2));
    }

    @Test
    void greaterOrEqualGivesTrueAndFalse() throws MyException {
        assertEquals(TRUE, eval(RelOp.GE, 2, 2));
        assertEquals(FALSE, eval(RelOp.GE, 1, 2));
    }

    @Test
    void comparesTheValuesOfVariables() throws MyException {
        symTable.put("a", new IntValue(5));
        symTable.put("b", new IntValue(7));
        IExp e = new RelationalExp(RelOp.LT, new VarExp("a"), new VarExp("b"));
        assertEquals(TRUE, e.eval(symTable));
    }

    @Test
    void nonIntegerFirstOperandThrows() {
        IExp e = new RelationalExp(RelOp.LT, new ValueExp(new BoolValue(true)), num(1));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable));
        assertEquals("first operand is not an integer", ex.getMessage());
    }

    @Test
    void nonIntegerSecondOperandThrows() {
        IExp e = new RelationalExp(RelOp.GE, num(1), new ValueExp(new BoolValue(false)));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable));
        assertEquals("second operand is not an integer", ex.getMessage());
    }

    @Test
    void toStringIsInInfixForm() {
        assertEquals("(a <= 7)", new RelationalExp(RelOp.LE, new VarExp("a"), num(7)).toString());
        assertEquals("(1 != 2)", new RelationalExp(RelOp.NE, num(1), num(2)).toString());
    }
}
