package interpreter.model.value;

import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.type.StringType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValueTest {
    @Test
    void getTypeReturnsCorrectType() {
        assertEquals(new IntType(), new IntValue(5).getType());
        assertEquals(new BoolType(), new BoolValue(true).getType());
        assertEquals(new StringType(), new StringValue("text").getType());
    }

    @Test
    void equalsWorksCorrectlyForRecords() {
        assertEquals(new IntValue(5), new IntValue(5));
        assertNotEquals(new IntValue(5), new IntValue(10));

        assertEquals(new BoolValue(true), new BoolValue(true));
        assertNotEquals(new BoolValue(true), new BoolValue(false));

        assertNotEquals(new IntValue(5), new BoolValue(true));
    }

    @Test
    void equalStringValuesAreEqualAndHaveTheSameHashCode() {
        StringValue a = new StringValue("test.in");
        StringValue b = new StringValue("test.in");
        assertNotSame(a, b);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void differentStringValuesAreNotEqual() {
        assertNotEquals(new StringValue("a.in"), new StringValue("b.in"));
        assertNotEquals(new StringValue("5"), new IntValue(5));
    }

    @Test
    void stringValueIsPrintedBetweenQuotes() {
        assertEquals("\"test.in\"", new StringValue("test.in").toString());
    }

    @Test
    void refValueHasTheTypeRefOfItsLocationType() {
        assertEquals(new RefType(new IntType()), new RefValue(1, new IntType()).getType());
        assertEquals(new RefType(new RefType(new IntType())),
                new RefValue(10, new RefType(new IntType())).getType());
    }

    @Test
    void refValuesAreEqualWhenAddressAndLocationTypeAreEqual() {
        assertEquals(new RefValue(1, new IntType()), new RefValue(1, new IntType()));
        assertNotEquals(new RefValue(1, new IntType()), new RefValue(2, new IntType()));
        assertNotEquals(new RefValue(1, new IntType()), new RefValue(1, new BoolType()));
    }

    @Test
    void refValueIsPrintedAsAddressAndLocationType() {
        assertEquals("(2, Ref(int))", new RefValue(2, new RefType(new IntType())).toString());
    }
}
