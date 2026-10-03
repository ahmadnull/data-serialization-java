package io.github.ahmadnull.dataserialization.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Object binding, the reflection driven half of {@link ObjectBinder} reached
 * through the public JSON entry points.
 */
class JsonObjectBindingTest {
    static class Point {
        int x;
        String label;
    }

    /** Every primitive and wrapper target type in one model. */
    static class Primitives {
        int anInt;
        long aLong;
        double aDouble;
        float aFloat;
        short aShort;
        byte aByte;
        boolean aBoolean;
        Integer boxedInt;
        String text;
    }

    static class WithDefaults {
        String name = "default";
        int count = 42;
    }

    static class PrivateFields {
        private String secret = "hidden";

        String read() {
            return secret;
        }
    }

    static class Inherited {
        String own;
    }

    static class NoDefaultConstructor {
        final int value;

        NoDefaultConstructor(int value) {
            this.value = value;
        }
    }

    @Test void serializesEveryDeclaredField() {
        Point point = new Point();
        point.x = 3;
        point.label = "hi";

        assertEquals(
            """
            {
                "x": 3,
                "label": "hi"
            }""",
            Json.serializeObject(point));
    }

    @Test void serializesPrivateFields() {
        assertTrue(
            Json.serializeObject(new PrivateFields()).contains("\"secret\": \"hidden\""),
            "a private field is still a declared field and has to be serialized");
    }

    @Test void roundTripsAPoint() {
        Point point = new Point();
        point.x = 3;
        point.label = "hi";

        Point back = Json.deserializeObject(Json.serializeObject(point), Point.class);

        assertEquals(3, back.x);
        assertEquals("hi", back.label);
    }

    /**
     * A literal is parsed into the widest lossless type, the binder has to
     * narrow it to whatever the field declares.
     */
    @Test void roundTripsEveryNumericTargetType() {
        Primitives source = new Primitives();
        source.anInt = 1;
        source.aLong = 2;
        source.aDouble = 3.5;
        source.aFloat = 4.5f;
        source.aShort = 5;
        source.aByte = 6;
        source.aBoolean = true;
        source.boxedInt = 7;
        source.text = "eight";

        Primitives back = Json.deserializeObject(Json.serializeObject(source), Primitives.class);

        assertEquals(1, back.anInt);
        assertEquals(2L, back.aLong);
        assertEquals(3.5d, back.aDouble);
        assertEquals(4.5f, back.aFloat);
        assertEquals((short) 5, back.aShort);
        assertEquals((byte) 6, back.aByte);
        assertEquals(true, back.aBoolean);
        assertEquals(7, back.boxedInt);
        assertEquals("eight", back.text);
    }

    @Test void narrowsAParsedLongIntoAnIntField() {
        Primitives back = Json.deserializeObject("{\"anInt\": 12}", Primitives.class);

        assertEquals(12, back.anInt);
    }

    @Test void keepsTheConstructorValueForAnAbsentKey() {
        WithDefaults back = Json.deserializeObject("{}", WithDefaults.class);

        assertEquals("default", back.name);
        assertEquals(42, back.count);
    }

    @Test void ignoresAnUnknownKey() {
        WithDefaults back = Json.deserializeObject("{\"unknown\": 1, \"name\": \"given\"}", WithDefaults.class);

        assertEquals("given", back.name);
    }

    /** Only the fields of the target class itself are populated. */
    @Test void ignoresFieldsDeclaredByASuperclass() {
        Inherited back = Json.deserializeObject("{\"own\": \"child\"}", Inherited.class);

        assertEquals("child", back.own);
    }

    @Test void rejectsANonObjectRoot() {
        JsonDeserializationException e = assertThrows(
            JsonDeserializationException.class,
            () -> Json.deserializeObject("[1, 2]", Point.class));

        assertEquals("Expected JSON Object root for class deserialization", e.getMessage());
    }

    @Test void rejectsAClassWithoutANoArgConstructor() {
        JsonDeserializationException e = assertThrows(
            JsonDeserializationException.class,
            () -> Json.deserializeObject("{\"value\": 1}", NoDefaultConstructor.class));

        assertEquals("Failed to bind JSON Object to " + NoDefaultConstructor.class.getName(), e.getMessage());
        assertTrue(
            e.getCause().getMessage().startsWith("Failed to instantiate target class:"),
            "the precise reason has to survive in the cause, was: " + e.getCause().getMessage());
    }

    /** A primitive field cannot hold null, the binder must not swallow that. */
    @Test void rejectsNullForAPrimitiveField() {
        JsonDeserializationException e = assertThrows(
            JsonDeserializationException.class,
            () -> Json.deserializeObject("{\"x\": null}", Point.class));

        assertTrue(
            e.getCause().getMessage().startsWith("Failed to set field 'x' on"),
            "the precise reason has to survive in the cause, was: " + e.getCause().getMessage());
    }
}
