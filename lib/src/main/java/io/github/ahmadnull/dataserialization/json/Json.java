package io.github.ahmadnull.dataserialization.json;

import java.util.Map;

import io.github.ahmadnull.dataserialization.internal.ObjectBinder;
import io.github.ahmadnull.dataserialization.internal.ObjectBinder.BindingException;

public class Json {
    // --- Serialization ---

    public static <T> String serializeObject(T object) {
        JsonSettings settings = new JsonSettings();
        return serializeObject(object, settings);
    }

    public static <T> String serializeObject(T object, JsonSettings settings) {
        Map<String, Object> map;
        try {
            map = ObjectBinder.toMap(object);
        } catch (BindingException e) {
            throw new IllegalStateException(
                "Failed to read the fields of " + object.getClass().getName(), e);
        }

        return serialize(map, settings);
    }

    public static String serialize(Object data) {
        JsonSettings settings = new JsonSettings();
        return serialize(data, settings);
    }

    public static String serialize(Object data, JsonSettings settings) {
        Serializer serializer = new Serializer(settings);
        serializer.serializeValue(data);
        return serializer.getSerializedString();
    }

    // --- Deserialization ---

    public static <T> T deserializeObject(String raw, Class<T> c) {
        Object parsed = deserialize(raw);
        if (!(parsed instanceof Map))
            throw new JsonDeserializationException("Expected JSON Object root for class deserialization");

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) parsed;

        try {
            return ObjectBinder.toObject(map, c);
        } catch (BindingException e) {
            throw new JsonDeserializationException("Failed to bind JSON Object to " + c.getName(), e);
        }
    }

    public static Object deserialize(String raw) {
        JsonSettings settings = new JsonSettings();
        return deserialize(raw, settings);
    }

    public static Object deserialize(
            String raw,
            JsonSettings settings
    ) {
        if (raw == null) throw new JsonDeserializationException("Raw Json string can not be null");

        Deserializer parser = new Deserializer(raw, settings);
        Object result = parser.deserializeValue();
        parser.skipWhitespace();
        if (parser.hasMore())
            throw new JsonDeserializationException("Unexpected trailing characters at position " + parser.index);

        return result;
    }
}
