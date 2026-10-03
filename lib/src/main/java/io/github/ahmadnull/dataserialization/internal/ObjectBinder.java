package io.github.ahmadnull.dataserialization.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reflection based conversion between plain objects and the
 * {@code Map}/{@code Collection} data model every format parses into.
 *
 * <p>Internal helper, not part of the public API. The package
 * {@code io.github.ahmadnull.dataserialization.internal} is deliberately absent
 * from the {@code exports} list of the module descriptor, which makes this class
 * unreachable for consumers of the library. Every format is free to change or
 * delete it in any release.
 *
 * <p>The failure modes are format independent, so they are reported as a
 * {@link BindingException} and translated by the calling format package into its
 * own exception type. Letting the exception escape instead would put an
 * internal, non exported type into the public signature.
 */
public final class ObjectBinder {
    private ObjectBinder() {}

    /**
     * Thrown when a field can neither be read nor written.
     *
     * <p>Always wrapped by the calling format, never thrown to library users.
     */
    public static class BindingException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public BindingException(String message) {
            super(message);
        }

        public BindingException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Read every declared field of an object into an insertion ordered map.
     *
     * <p>Superclass fields are not included and static fields are read as
     * declared by {@link Class#getDeclaredFields()}, which matches the previous
     * behaviour of the JSON binder.
     *
     * @throws BindingException if a field cannot be made accessible or read
     */
    public static Map<String, Object> toMap(Object object) {
        Class<?> type = object.getClass();
        Map<String, Object> map = new LinkedHashMap<>();

        for (Field field : type.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                map.put(field.getName(), field.get(object));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new BindingException(
                    "Failed to read field '" + field.getName() + "' on " + type.getName(), e);
            }
        }

        return map;
    }

    /**
     * Populate a new instance of {@code targetClass} from a parsed map.
     *
     * <p>Keys without a matching field are ignored, and fields without a
     * matching key keep the value assigned by the constructor. Inherited fields
     * are not populated.
     *
     * @throws BindingException if the class cannot be instantiated or a field
     *                          cannot be written
     */
    public static <T> T toObject(Map<?, ?> map, Class<T> targetClass) {
        T instance;
        try {
            Constructor<T> constructor = targetClass.getDeclaredConstructor();
            // A class that declares no constructor, or a non public one, has a
            // package private default constructor. The caller of this library
            // sits in another package and in another module, so the implicit
            // accessibility has to be widened before it can be invoked.
            constructor.setAccessible(true);
            instance = constructor.newInstance();
        } catch (ReflectiveOperationException | RuntimeException e) {
            throw new BindingException(
                "Failed to instantiate target class: " + targetClass.getName(), e);
        }

        for (Field field : targetClass.getDeclaredFields()) {
            String name = field.getName();
            if (!map.containsKey(name)) continue;

            try {
                field.setAccessible(true);
                field.set(instance, Types.coerce(map.get(name), field.getType()));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new BindingException(
                    "Failed to set field '" + name + "' on " + targetClass.getName(), e);
            }
        }

        return instance;
    }
}
