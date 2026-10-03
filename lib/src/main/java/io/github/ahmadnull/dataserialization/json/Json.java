package io.github.ahmadnull.dataserialization.json;

import java.util.Collection;
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
        StringBuilder sb = new StringBuilder();
        serializeValue(data, settings.multiline(), settings.indentation(), settings.level(), sb);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static void serializeValue(
            Object value,
            boolean multiline,
            int indentation, int level,
            StringBuilder sb
    ) {
        switch(value) {
            case String s -> sb.append('"').append(Helpers.escapeJson(s)).append('"');
            case Number n -> sb.append(n);
            case Boolean b -> sb.append(b);
            case Map m -> serializeMap(m, multiline, indentation, level, sb);
            case Collection l -> serializeCollection(l, multiline, indentation, level, sb);
            case Object[] a -> serializeArray(a, multiline, indentation, level, sb);
            case null -> sb.append("null");
            case Object other -> throw new JsonValueException(other);
        }
    }

    private static void serializeMap(
            Map<String, Object> map,
            boolean multiline,
            int indentation, int level,
            StringBuilder sb
    ) {
        sb.append('{');

        if (!map.isEmpty()) {
            boolean first = true;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (!first) sb.append(',');
                if (multiline) sb.append('\n').append(" ".repeat(indentation * level));
                else if (!first) sb.append(' ');

                sb.append('"').append(Helpers.escapeJson(String.valueOf(entry.getKey()))).append("\": ");
                serializeValue(entry.getValue(), multiline, indentation, level + 1, sb);
                first = false;
            }
            if (multiline) sb.append('\n').append(" ".repeat(indentation * (level - 1)));
        }

        sb.append('}');
    }

    private static void serializeCollection(
            Collection<Object> Collection,
            boolean multiline,
            int indentation, int level,
            StringBuilder sb
    ) {
        sb.append('[');

        if (Collection.iterator().hasNext()) {
            boolean first = true;
            for (Object item : Collection) {
                if (!first) sb.append(',');
                if (multiline) sb.append('\n').append(" ".repeat(indentation * level));
                else if (!first) sb.append(' ');

                serializeValue(item, multiline, indentation, level + 1, sb);
                first = false;
            }
            if (multiline) sb.append('\n').append(" ".repeat(indentation * (level - 1)));
        }

        sb.append(']');
    }

    private static void serializeArray(
            Object[] array,
            boolean multiline,
            int indentation, int level,
            StringBuilder sb
    ) {
        sb.append('[');

        if (array.length > 0) {
            boolean first = true;
            for (Object item : array) {
                if (!first) sb.append(',');
                if (multiline) sb.append('\n').append(" ".repeat(indentation * level));
                else if (!first) sb.append(' ');

                serializeValue(item, multiline, indentation, level + 1, sb);
                first = false;
            }
            if (multiline) sb.append('\n').append(" ".repeat(indentation * (level - 1)));
        }

        sb.append(']');
    }

    // --- Deserialization ---

    public static <T> T deserializeObject(String raw, Class<T> c) {
        Object parsed = deserialize(raw);
        if (!(parsed instanceof Map))
            throw new JsonParsingException("Expected JSON Object root for class deserialization");

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) parsed;

        try {
            return ObjectBinder.toObject(map, c);
        } catch (BindingException e) {
            throw new JsonParsingException("Failed to bind JSON Object to " + c.getName(), e);
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
        if (raw == null) throw new JsonParsingException("Raw Json string can not be null");

        Parser parser = new Parser(raw, settings);
        Object result = parser.parseValue();
        parser.skipWhitespace();
        if (parser.hasMore())
            throw new JsonParsingException("Unexpected trailing characters at position " + parser.index);

        return result;
    }
}
