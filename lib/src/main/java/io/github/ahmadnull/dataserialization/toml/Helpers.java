package io.github.ahmadnull.dataserialization.toml;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

final class Helpers {
    static boolean isTableLike(Object v) {
        if (v instanceof Map) return true;                       // empty map → header
        if (!(v instanceof Collection<?> c) || c.isEmpty()) return false;
        if (!c.stream().allMatch(e -> e instanceof Map)) return false; // every element
        return true;
    }

    static Map<String, Object> asMap(Object v) {
        if (v instanceof Map m) return m;
        throw new TomlSerializationException(v);
    }

    private static final Pattern BARE_KEY_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    static String keyName(String bare) {
        if (BARE_KEY_PATTERN.matcher(bare).matches()) return bare;
        return '"' + escapeString(bare) + '"';
    }

    static String escapeString(String input) {
        if (input == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c <= 0x1F || c == 0x7F) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        return sb.toString();
    }
}
