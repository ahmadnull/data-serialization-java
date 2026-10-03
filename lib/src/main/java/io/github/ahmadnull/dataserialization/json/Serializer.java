package io.github.ahmadnull.dataserialization.json;

import java.util.Collection;
import java.util.Map;

class Serializer {
    final boolean multiline;
    final int indentation;
    final int maxDepth;
    final StringBuilder sb;
    int level = 1;
    int depth = 0;

    Serializer(JsonSettings settings) {
        this.multiline = settings.multiline();
        this.indentation = settings.indentation();
        this.maxDepth = settings.maxDepth();
        this.level = settings.level();
        sb = new StringBuilder();
    }

    String getSerializedString() {
        return this.sb.toString();
    }

    @SuppressWarnings("unchecked")
    void serializeValue(Object data) {
        try {
            if(++depth > maxDepth)
                throw new JsonSerializationException("Maximum nesting depth of " + maxDepth + " exceeded");

            switch(data) {
                case String s -> sb.append('"').append(Helpers.escapeJson(s)).append('"');
                case Number n -> sb.append(n);
                case Boolean b -> sb.append(b);
                case Map m -> serializeMap(m);
                case Collection l -> serializeCollection(l);
                case Object[] a -> serializeArray(a);
                case null -> sb.append("null");
                case Object other -> throw new JsonSerializationException(other);
            }
        } finally {
            depth--;
        }
    }

    void serializeMap(Map<String, Object> map) {
        sb.append('{');

        if (!map.isEmpty()) {
            boolean first = true;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (!first) sb.append(',');
                if (multiline) sb.append('\n').append(" ".repeat(indentation * level));
                else if (!first) sb.append(' ');

                sb.append('"').append(Helpers.escapeJson(String.valueOf(entry.getKey()))).append("\": ");
                level++;
                serializeValue(entry.getValue());
                level--;
                first = false;
            }
            if (multiline) sb.append('\n').append(" ".repeat(indentation * (level - 1)));
        }

        sb.append('}');
    }

    void serializeCollection(Collection<Object> Collection) {
        sb.append('[');

        if (Collection.iterator().hasNext()) {
            boolean first = true;
            for (Object item : Collection) {
                if (!first) sb.append(',');
                if (multiline) sb.append('\n').append(" ".repeat(indentation * level));
                else if (!first) sb.append(' ');

                level++;
                serializeValue(item);
                level--;
                first = false;
            }
            if (multiline) sb.append('\n').append(" ".repeat(indentation * (level - 1)));
        }

        sb.append(']');
    }

    void serializeArray(Object[] array) {
        sb.append('[');

        if (array.length > 0) {
            boolean first = true;
            for (Object item : array) {
                if (!first) sb.append(',');
                if (multiline) sb.append('\n').append(" ".repeat(indentation * level));
                else if (!first) sb.append(' ');

                level++;
                serializeValue(item);
                level--;
                first = false;
            }
            if (multiline) sb.append('\n').append(" ".repeat(indentation * (level - 1)));
        }

        sb.append(']');
    }
}
