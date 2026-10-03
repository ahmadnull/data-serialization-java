package io.github.ahmadnull.dataserialization.toml;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

class Serializer {
    final int maxDepth;
    final StringBuilder sb;
    int depth = 0;

    Serializer(TomlSettings settings) {
        this.maxDepth = settings.maxDepth();
        this.sb = new StringBuilder();
    }

    String getSerializedString() {
        return this.sb.toString();
    }

    void serializeRoot(Map<String, Object> root) {
        try {
            if (++depth > maxDepth)
                throw new TomlSerializationException("Maximum nesting depth of " + maxDepth + " exceeded");
            serializeMap(root, "");
        } finally {
            depth--;
        }
    }

    // Serialize Java Map<String, Object> into TOML Table
    void serializeMap(Map<String, Object> map, String path) {
        // pass 1: plain values
        for (var e : map.entrySet()) {
            if (Helpers.isTableLike(e.getValue())) continue;
            sb.append(Helpers.keyName(e.getKey())).append(" = ");
            serializeValue(e.getValue());
            sb.append('\n');
        }

        // pass 2: sub-tables
        for (var e : map.entrySet()) {
            if (!Helpers.isTableLike(e.getValue())) continue;
            String subPath = path.isEmpty() ? Helpers.keyName(e.getKey()) : path + '.' + Helpers.keyName(e.getKey());
            Object value = e.getValue();
            try {
                if (++depth > maxDepth)
                    throw new TomlSerializationException("Maximum nesting depth of " + maxDepth + " exceeded");
                if (value instanceof Map nestedTable) {
                    sb.append('[').append(subPath).append("]\n");
                    serializeMap(nestedTable, subPath);
                } else {
                    for (Object o : (Collection) value) {
                        sb.append("[[").append(subPath).append("]]\n");
                        serializeMap(Helpers.asMap(o), subPath);
                    }
                }
            } finally {
                depth--;
            }
        }
    }

    void serializeValue(Object value) {
        try {
            if (++depth > maxDepth)
                throw new TomlSerializationException("Maximum nesting depth of " + maxDepth + " exceeded");

            switch (value) {
                case String s -> sb.append('"').append(Helpers.escapeString(s)).append('"');
                case Boolean b       -> sb.append(b);
                case Double d        -> serializeDouble(d);
                case Float f         -> serializeDouble(f.doubleValue());
                case BigDecimal d    -> serializeDecimal(d);
                case Number n        -> sb.append(n);
                case ZonedDateTime t -> sb.append(t.toOffsetDateTime());
                case Temporal t      -> sb.append(t);
                case Map m           -> serializeMapAsInlineTable(m);
                case Collection c    -> serializeCollection(c);
                case Object[] a      -> serializeArray(a);
                case null            -> throw TomlSerializationException.ofNull();
                default              -> throw new TomlSerializationException(value);
            }
        } finally {
            depth--;
        }
    }

    void serializeArray(Object[] a) {
        sb.append('[');
        boolean first = true;
        for (Object item : a) {
            if(!first) sb.append(", ");
            serializeValue(item);
            first = false;
        }
        sb.append(']');
    }

    void serializeCollection(Collection<Object> c) {
        sb.append('[');
        boolean first = true;
        for (Object item : c) {
            if(!first) sb.append(", ");
            serializeValue(item);
            first = false;
        }
        sb.append(']');
    }

    void serializeMapAsInlineTable(Map<String, Object> map) {
        sb.append('{');
        boolean first = true;
        for (var e : map.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(Helpers.keyName(e.getKey())).append(" = ");
            serializeValue(e.getValue());
            first = false;
        }
        sb.append('}');
    }

    void serializeDecimal(BigDecimal d) {
        String text = d.toPlainString();
        sb.append(text);
        if (text.indexOf('.') < 0)
            sb.append(".0");
    }

    void serializeDouble(double d) {
        if (Double.isNaN(d)) {
            sb.append("nan");
            return;
        }

        if (Double.isInfinite(d)) {
            sb.append(d > 0 ? "inf" : "-inf");
            return;
        }

        String text = Double.toString(d);
        sb.append(text);

        if (text.indexOf('.') < 0 && text.indexOf('e') < 0 && text.indexOf('E') < 0)
            sb.append(".0");
    }
}
