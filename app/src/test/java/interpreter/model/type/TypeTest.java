package interpreter.model.type;

import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
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

    @Test
    void refTypeDefaultValueIsTheNullReference() {
        assertEquals(new RefValue(0, new IntType()), new RefType(new IntType()).defaultValue());
        assertEquals(new RefValue(0, new RefType(new BoolType())),
                new RefType(new RefType(new BoolType())).defaultValue());
    }

    @Test
    void nestedRefTypesAreComparedRecursively() {
        IType refRefInt = new RefType(new RefType(new IntType()));
        assertEquals(refRefInt, new RefType(new RefType(new IntType())));
        assertEquals(refRefInt.hashCode(), new RefType(new RefType(new IntType())).hashCode());
        assertNotEquals(refRefInt, new RefType(new IntType()));
        assertNotEquals(refRefInt, new RefType(new RefType(new BoolType())));
        assertNotEquals(new RefType(new IntType()), new IntType());
    }

    @Test
    void refTypeIsPrintedWithItsInnerType() {
        assertEquals("Ref(Ref(int))", new RefType(new RefType(new IntType())).toString());
    }
}
