package io.github.ahmadnull.dataserialization.internal;

/**
 * Type coercion shared by the format packages.
 *
 * <p>Internal helper, not part of the public API. The package
 * {@code io.github.ahmadnull.dataserialization.internal} is deliberately absent
 * from the {@code exports} list of the module descriptor, which makes this class
 * unreachable for consumers of the library. Every format is free to change or
 * delete it in any release.
 */
public final class Types {
    private Types() {}

    /**
     * Narrow a parsed value to the primitive or wrapper type a field declares.
     *
     * <p>Parsers are free to produce the widest lossless numeric type for a
     * literal, so a JSON {@code 1} arrives as an {@link Integer} even when the
     * target field is a {@code double}. The conversion stays within the
     * {@link Number} hierarchy; anything else, and any value that cannot be
     * narrowed, is returned unchanged and left to
     * {@link java.lang.reflect.Field#set} to reject.
     *
     * @param value      the parsed value, may be null
     * @param targetType the declared type of the destination field
     * @return the coerced value, or null when {@code value} is null
     */
    public static Object coerce(Object value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType.isInstance(value)) return value;

        if (value instanceof Number num) {
            if (targetType == int.class || targetType == Integer.class) return num.intValue();
            if (targetType == long.class || targetType == Long.class) return num.longValue();
            if (targetType == double.class || targetType == Double.class) return num.doubleValue();
            if (targetType == float.class || targetType == Float.class) return num.floatValue();
            if (targetType == short.class || targetType == Short.class) return num.shortValue();
            if (targetType == byte.class || targetType == Byte.class) return num.byteValue();
        }

        return value;
    }
}
