package io.github.ahmadnull.dataserialization.json;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

class Deserializer {
    final String src;
    final int maxDepth;
    final Supplier<Map<String, Object>> mapFactory;
    final Supplier<Collection<Object>> collectionFactory;
    int index = 0;
    int depth = 0;

    Deserializer(String src, JsonSettings settings) {
        this.src = src;
        this.maxDepth = settings.maxDepth();
        this.mapFactory = settings.mapFactory();
        this.collectionFactory = settings.collectionFactory();
    }

    boolean hasMore() {
        return index < src.length();
    }

    boolean isDigit() {
        char c = peek();
        return c >= '0' && c <= '9';
    }

    boolean isJsonWhitespace() {
        char c = peek();
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    char peek() {
        return hasMore() ? src.charAt(index) : '\0';
    }

    char next() {
        return src.charAt(index++);
    }

    void skipWhitespace() {
        while(hasMore() && isJsonWhitespace()) index++;
    }

    Object deserializeValue() {
        skipWhitespace();
        if (!hasMore()) throw new JsonDeserializationException("Unexpected end of Input");

        char c = peek();
        if (depth >= maxDepth)
            throw new JsonDeserializationException("Maximum nesting depth of " + maxDepth + " exceeded at position " + index);
        try {
            depth++;
            return switch (c) {
                case '{' -> deserializeObject();
                case '[' -> deserializeArray();
                case '"' -> deserializeString();
                case 't', 'f' -> deserializeBoolean();
                case 'n' -> deserializeNull();
                case '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> deserializeNumber();
                default -> throw new JsonDeserializationException("Unexpected character '" + c + "' at position " + index);
            };
        } finally {
            depth--;
        }
    }

    Map<String, Object> deserializeObject() {
        Map<String, Object> map = mapFactory.get();
        next(); // consume '{'
        skipWhitespace();

        if (peek() == '}') {
            next(); // consume '}'
            return map; // empty object
        }

        while(hasMore()) {
            skipWhitespace();
            if (peek() != '"')
                throw new JsonDeserializationException("Expected string key in object at position " + index);

            String key = deserializeString();
            skipWhitespace();

            if (peek() != ':')
                throw new JsonDeserializationException("Expected ':' after key at position " + index);
            next(); // consume ':'

            Object value = deserializeValue();
            map.put(key, value);

            skipWhitespace();
            char c = peek();
            if (c == '}') {
                next(); // consume '}'
                return map;
            } else if (c == ',') {
                next(); // consume ','
            } else throw new JsonDeserializationException("Expected ',' or '}' in object at position " + index);
        }

        throw new JsonDeserializationException("Unterminated object starting at position " + index);
    }

    Collection<Object> deserializeArray() {
        Collection<Object> collection = collectionFactory.get();
        next(); // consume '['
        skipWhitespace();

        if (peek() == ']') {
            next(); // consume ']'
            return collection; // empty array
        }

        while(hasMore()) {
            Object value = deserializeValue();
            collection.add(value);

            skipWhitespace();
            char c = peek();
            if (c == ']') {
                next(); // consume ']'
                return collection;
            } else if (c == ',') {
                next(); // consume ','
            } else throw new JsonDeserializationException("Expected ',' or ']' in array at position " + index);
        }

        throw new JsonDeserializationException("Unterminated array starting at position " + index);
    }

    String deserializeString() {
        next(); // consume opening quote '"'
        StringBuilder sb = new StringBuilder();

        while(hasMore()) {
            char c = next();
            if (c == '"') // closing quote '"'
                return sb.toString();

            if (c == '\\') {
                if (!hasMore())
                    throw new JsonDeserializationException("Unterminated escape sequence in string");

                char esc = next();
                switch (esc) {
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/' -> sb.append('/');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case 'u' -> {
                        if (index + 4 > src.length()) {
                            throw new JsonDeserializationException("Invalid unicode escape sequence");
                        }
                        String hex = src.substring(index, index + 4);
                        index += 4;
                        try {
                            sb.append((char) Integer.parseInt(hex, 16));
                        } catch (NumberFormatException e) {
                            throw new JsonDeserializationException("Invalid hex in unicode sequence: \\u" + hex);
                        }
                    }
                    default -> throw new JsonDeserializationException("Invalid escape sequence: \\" + esc);
                }
            } else {
                if (c < 0x20)
                    throw new JsonDeserializationException(
                        "Unescaped control character U+" + String.format("%04X", (int) c) + " at position " + (index - 1));
                sb.append(c);
            }
        }

        throw new JsonDeserializationException("Unterminated string literal");
    }

    Number deserializeNumber() {
        int start = index;
        if (peek() == '-') next();
        if (!isDigit())
            throw new JsonDeserializationException("Invalid character at position " + index + ". Expected a digit");

        if (peek() == '0') {
            next();
            if (isDigit())
                throw new JsonDeserializationException("Invalid digit at position " + index + ". Numeric values can not have leading zeros");
        } else while (hasMore() && isDigit()) next();

        boolean isFloatingPoint = false;
        if (hasMore() && peek() == '.') {
            isFloatingPoint = true;
            next(); // consume '.'
            if (!isDigit())
                throw new JsonDeserializationException("Invalid character at position " + index + ". Expected at least one digit after the decimal point");
            while (hasMore() && isDigit()) {
                next();
            }
        }

        if (hasMore() && (peek() == 'e' || peek() == 'E')) {
            isFloatingPoint = true;
            next();
            if (hasMore() && (peek() == '+' || peek() == '-')) {
                next();
            }
            if (!isDigit())
                throw new JsonDeserializationException("Invalid character at position " + index + ". Expected at least one digit after the scientific notation (e/E)");
            while (hasMore() && isDigit()) {
                next();
            }
        }

        String numStr = src.substring(start, index);
        try {
            if (isFloatingPoint) {
                return Double.parseDouble(numStr);
            } else {
                long val = Long.parseLong(numStr);
                if (val >= Integer.MIN_VALUE && val <= Integer.MAX_VALUE) {
                    return (int) val;
                }
                return val;
            }
        } catch (NumberFormatException e) {
            throw new JsonDeserializationException("Invalid numeric value: " + numStr);
        }
    }

    Boolean deserializeBoolean() {
        if (src.startsWith("true", index)) {
            index += 4;
            return true;
        } else if (src.startsWith("false", index)) {
            index += 5;
            return false;
        }
        throw new JsonDeserializationException("Invalid boolean at position " + index);
    }

    Object deserializeNull() {
        if (src.startsWith("null", index)) {
            index += 4;
            return null;
        }
        throw new JsonDeserializationException("Invalid null token at position " + index);
    }
}
