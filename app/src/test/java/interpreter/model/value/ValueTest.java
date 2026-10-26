package interpreter.model.value;

import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValueTest {
    @Test
    void getTypeReturnsCorrectType() {
        assertEquals(new IntType(), new IntValue(5).getType());
        assertEquals(new BoolType(), new BoolValue(true).getType());
    }

    @Test
    void equalsWorksCorrectlyForRecords() {
        assertEquals(new IntValue(5), new IntValue(5));
        assertNotEquals(new IntValue(5), new IntValue(10));

        assertEquals(new BoolValue(true), new BoolValue(true));
        assertNotEquals(new BoolValue(true), new BoolValue(false));

        assertNotEquals(new IntValue(5), new BoolValue(true));
    }
}