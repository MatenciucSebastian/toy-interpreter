package interpreter.model.type;

import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TypeTest {
    @Test
    void defaultValuesAreCorrect() {
        assertEquals(new IntValue(0), new IntType().defaultValue());
        assertEquals(new BoolValue(false), new BoolType().defaultValue());
    }

    @Test
    void stringDefaultValueIsTheEmptyString() {
        assertEquals(new StringValue(""), new StringType().defaultValue());
    }

    @Test
    void equalsWorksCorrectlyForRecords() {
        assertEquals(new IntType(), new IntType());
        assertEquals(new BoolType(), new BoolType());
        assertNotEquals(new IntType(), new BoolType());
    }

    @Test
    void stringTypesAreEqualAndDifferFromTheOtherTypes() {
        assertEquals(new StringType(), new StringType());
        assertEquals(new StringType().hashCode(), new StringType().hashCode());
        assertNotEquals(new StringType(), new IntType());
        assertNotEquals(new StringType(), new BoolType());
    }

    @Test
    void stringTypeIsPrintedAsString() {
        assertEquals("string", new StringType().toString());
    }
}
