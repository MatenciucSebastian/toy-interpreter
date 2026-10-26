package interpreter.model.type;

import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TypeTest {
    @Test
    void defaultValuesAreCorrect() {
        assertEquals(new IntValue(0), new IntType().defaultValue());
        assertEquals(new BoolValue(false), new BoolType().defaultValue());
    }

    @Test
    void equalsWorksCorrectlyForRecords() {
        assertEquals(new IntType(), new IntType());
        assertEquals(new BoolType(), new BoolType());
        assertNotEquals(new IntType(), new BoolType());
    }
}