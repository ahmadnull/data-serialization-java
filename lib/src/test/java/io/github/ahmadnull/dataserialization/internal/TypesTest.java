package io.github.ahmadnull.dataserialization.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/**
 * The coercion matrix of {@link Types}.
 *
 * <p>This test lives in the internal package on purpose, it only compiles
 * because the test source set is patched into the module and may therefore read
 * a package the module descriptor does not export.
 */
class TypesTest {
    @Test void nullStaysNull() {
        assertNull(Types.coerce(null, int.class));
        assertNull(Types.coerce(null, String.class));
    }

    @Test void alreadyMatchingValueIsReturnedUnchanged() {
        Object value = 7;

        assertSame(value, Types.coerce(value, int.class));
        assertSame(value, Types.coerce(value, Number.class));
        assertSame(value, Types.coerce(value, Object.class));
    }

    @Test void nonNumericValueIsReturnedUnchanged() {
        Object text = "7";

        assertSame(text, Types.coerce(text, int.class));
        assertSame(text, Types.coerce(text, Integer.class));
    }

    @Test void numericValueIsReturnedUnchangedForUnrelatedTargetType() {
        Object value = 7;

        assertSame(value, Types.coerce(value, String.class));
    }

    /**
     * Every numeric target type has to be reachable from every numeric source
     * type, a parser is free to hand out the widest lossless type for a
     * literal while the destination field declares something narrower.
     */
    @ParameterizedTest(name = "coerce({0}, {1}) == {2}")
    @MethodSource("numericTargets")
    void narrowsToEveryNumericTargetType(Object value, Class<?> targetType, Object expected) {
        assertEquals(expected, Types.coerce(value, targetType));
    }

    static Stream<Arguments> numericTargets() {
        return Stream.of(
            // int and its wrapper
            Arguments.of(7, int.class, 7),
            Arguments.of(7L, int.class, 7),
            Arguments.of(7.9, int.class, 7),
            Arguments.of(7.9d, Integer.class, 7),
            // long, a parser has to hand out long for a value above int range
            Arguments.of(2147483648L, int.class, -2147483648),
            Arguments.of(2147483648L, long.class, 2147483648L),
            Arguments.of(2147483648L, Long.class, 2147483648L),
            Arguments.of(7.9, long.class, 7L),
            // double
            Arguments.of(7, double.class, 7.0d),
            Arguments.of(7L, double.class, 7.0d),
            Arguments.of(7.9, double.class, 7.9d),
            Arguments.of(7.9d, Double.class, 7.9d),
            // float, the narrowest numeric target, note the precision loss
            Arguments.of(7, float.class, 7.0f),
            Arguments.of(7.9, float.class, 7.9f),
            Arguments.of(7.9d, Float.class, 7.9f),
            // short and byte
            Arguments.of(7, short.class, (short) 7),
            Arguments.of(7L, Short.class, (short) 7),
            Arguments.of(7, byte.class, (byte) 7),
            Arguments.of(7L, Byte.class, (byte) 7)
        );
    }

    /**
     * The conversion goes through the {@link Number} accessors and therefore
     * does not detect an overflow, so it silently truncates a long into an int
     * and wraps a short into a byte. A caller that cares has to range check.
     */
    @Test void narrowingOverflowIsNotDetected() {
        assertEquals((byte) (Byte.MAX_VALUE + 1), Types.coerce(Byte.MAX_VALUE + 1, byte.class));
        assertEquals((short) (Short.MAX_VALUE + 1), Types.coerce(Short.MAX_VALUE + 1, short.class));
        assertEquals(7L, Types.coerce(7.9, long.class), "a fractional value truncates towards zero");
    }
}
