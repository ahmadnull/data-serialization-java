package io.github.ahmadnull.dataserialization.toml;

import java.util.Map;
import io.github.ahmadnull.dataserialization.internal.ObjectBinder;
import io.github.ahmadnull.dataserialization.internal.ObjectBinder.BindingException;

public class Toml {
    // --- Serialization ---

    public static <T> String serializeObject(T object) {
        TomlSettings settings = new TomlSettings();
        return serializeObject(object, settings);
    }

    public static <T> String serializeObject(T object, TomlSettings settings) {
        Map<String, Object> map;
        try {
            map = ObjectBinder.toMap(object);
        } catch (BindingException e) {
            throw new IllegalStateException(
                "Failed to read the fields of " + object.getClass().getName(), e);
        }

        return serialize(map, settings);
    }

    public static String serialize(Map<String, Object> data) {
        TomlSettings settings = new TomlSettings();
        return serialize(data, settings);
    }

    public static String serialize(Map<String, Object> data, TomlSettings settings) {
        Serializer serializer = new Serializer(settings);
        serializer.serializeRoot(data);
        return serializer.getSerializedString();
    }

}
