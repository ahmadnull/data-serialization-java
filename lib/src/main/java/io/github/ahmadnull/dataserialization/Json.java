package io.github.ahmadnull.dataserialization;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public class Json {
	private static class Helpers {
		private static String escapeJson(String input) {
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
	                    if (c <= 0x1F) sb.append(String.format("\\u%04x", (int) c));
	                    else sb.append(c);
	                }
	            }
	        }
	        return sb.toString();
	    }

		private static Object coerceType(Object value, Class<?> targetType) {
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

	public static class JsonValueException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public JsonValueException(Object value) {
			super(value.toString() + ": is not a valid JSON value\n" +
		         "Valid JSON values are: String, Integer, Double, Boolean, Map, Collection, and null.");
		}
	}

	public static class JsonParsingException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public JsonParsingException(String string) {
			super(string);
		}

		public JsonParsingException(String string, Exception e) {
			super(string, e);
		}
	}

	// Default Values
	private static boolean multilineDefault = true;
	private static int indentationDefault = 4;
	private static int levelDefault = 1;
	private static Supplier<Map<String, Object>> mapFactoryDefault = HashMap::new;
	private static Supplier<Collection<Object>> collectionFactoryDefault = ArrayList::new;

	public static <T> String serializeObject(T object, boolean multiline, int indentation, int level) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				map.put(field.getName(), field.get(object));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		return serialize(map, multiline, indentation, level);
	}

	public static String serialize(Object data) {
		return serialize(data, multilineDefault, indentationDefault, levelDefault);
	}

	public static String serialize(Object data, boolean multiline) {
		return serialize(data, multiline, indentationDefault, levelDefault);
	}

	public static String serialize(Object data, boolean multiline, int indentation) {
		return serialize(data, multiline, indentation, levelDefault);
	}

	public static String serialize(Object data, boolean multiline, int indentation, int level) {
		StringBuilder sb = new StringBuilder();
		serializeValue(data, multiline, indentation, level, sb);
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
			case Object other -> throw new Json.JsonValueException(other);
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

    @SuppressWarnings("unchecked")
	public static <T> T deserializeObject(String raw, Class<T> c) {
		Object parsed = deserialize(raw);
        if (!(parsed instanceof Map))
            throw new JsonParsingException("Expected JSON Object root for class deserialization");

		Map<String, Object> map = (Map<String, Object>) parsed;

		T result;
	    try {
	        result = c.getDeclaredConstructor().newInstance();
	    } catch (Exception e) {
	        throw new JsonParsingException("Failed to instantiate target class: " + c.getName(), e);
	    }

	    for (Field field : c.getDeclaredFields()) {
	        if (map.containsKey(field.getName())) {
	            Object value = map.get(field.getName());
	            field.setAccessible(true);

	            try {
	                if (value != null) {
	                    value = Helpers.coerceType(value, field.getType());
	                }
	                field.set(result, value);
	            } catch (Exception e) {
	                throw new JsonParsingException("Failed to set field '" + field.getName() + "' on " + c.getName(), e);
	            }
	        }
	    }

		return result;
	}

	public static <C extends Collection<Object>> Object deserialize(String raw) {
		return deserialize(raw, mapFactoryDefault, collectionFactoryDefault);
	}

	public static <C extends Collection<Object>> Object deserializeWithcollectionFactory(
			String raw,
			Supplier<C> collectionFactory
	) {
		return deserialize(raw, mapFactoryDefault, collectionFactory);
	}

	public static <M extends Map<String, Object>> Object deserializeWithMapFactory(
			String raw,
			Supplier<M> mapFactory
	) {
		return deserialize(raw, mapFactory, collectionFactoryDefault);
	}

	public static <M extends Map<String, Object>, C extends Collection<Object>> Object deserialize(
			String raw,
			Supplier<M> mapFactory,
			Supplier<C> collectionFactory
	) {
		if (raw == null) throw new JsonParsingException("Raw Json string can not be null");

		Parser parser = new Parser(raw);
		Object result = parser.parseValue(mapFactory, collectionFactory);
		parser.skipWhitespace();
		if (parser.hasMore())
			throw new JsonParsingException("Unexpected trailing characters at position " + parser.index);

		return result;
	}

	private static class Parser {
		private final String src;
		private int index = 0;

		Parser(String src) {
			this.src = src;
		}

		boolean hasMore() {
			return index < src.length();
		}

		char peek() {
			return hasMore() ? src.charAt(index) : '\0';
		}

		char next() {
			return src.charAt(index++);
		}

		void skipWhitespace() {
			while(hasMore() && Character.isWhitespace(peek())) index++;
		}

		<M extends Map<String, Object>, C extends Collection<Object>> Object parseValue(
				Supplier<M> mapFactory,
				Supplier<C> collectionFactory
		) {
			skipWhitespace();
			if (!hasMore()) throw new JsonParsingException("Unexpected end of Input");

			char c = peek();
			if (c == '{') return parseObject(mapFactory, collectionFactory);
            if (c == '[') return parseArray(mapFactory, collectionFactory);
            if (c == '"') return parseString();
            if (c == 't' || c == 'f') return parseBoolean();
            if (c == 'n') return parseNull();
            if (c == '-' || (c >= '0' && c <= '9')) return parseNumber();

            throw new JsonParsingException("Unexpected character '" + c + "' at position " + index);
		}

		<M extends Map<String, Object>, C extends Collection<Object>> M parseObject(
				Supplier<M> mapFactory,
				Supplier<C> collectionFactory
		) {
			M map = mapFactory.get();
			next(); // consume '{'
			skipWhitespace();

			if (peek() == '}') {
				next(); // consume '}'
				return map; // empty object
			}

			while(hasMore()) {
				skipWhitespace();
				if (peek() != '"')
					throw new JsonParsingException("Expected string key in object at position " + index);

				String key = parseString();
				skipWhitespace();

				if (peek() != ':')
					throw new JsonParsingException("Expected ':' after key at position " + index);
				next(); // consume ':'

				Object value = parseValue(mapFactory, collectionFactory);
				map.put(key, value);

				skipWhitespace();
				char c = peek();
				if (c == '}') {
					next(); // consume '}'
					return map;
				} else if (c == ',') {
					next(); // consume ','
				} else throw new JsonParsingException("Expected ',' or '}' in object at position " + index);
			}

			throw new JsonParsingException("Unterminated object starting at position " + index);
		}

		<M extends Map<String, Object>, C extends Collection<Object>> C parseArray(
				Supplier<M> mapFactory,
				Supplier<C> collectionFactory
		) {
			C collection = collectionFactory.get();
			next(); // consume '['
			skipWhitespace();

			if (peek() == ']') {
				next(); // consume ']'
				return collection; // empty array
			}

			while(hasMore()) {
				Object value = parseValue(mapFactory, collectionFactory);
				collection.add(value);

				skipWhitespace();
				char c = peek();
				if (c == ']') {
					next(); // consume ']'
					return collection;
				} else if (c == ',') {
					next(); // consume ','
				} else throw new JsonParsingException("Expected ',' or ']' in array at position " + index);
			}

			throw new JsonParsingException("Unterminated array starting at position " + index);
		}

		String parseString() {
			next(); // consume opening quote '"'
			StringBuilder sb = new StringBuilder();

			while(hasMore()) {
				char c = next();
				if (c == '"') // closing quote '"'
					return sb.toString();

				if (c == '\\') {
					if (!hasMore())
						throw new JsonParsingException("Unterminated escape sequence in string");

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
	                            throw new JsonParsingException("Invalid unicode escape sequence");
	                        }
	                        String hex = src.substring(index, index + 4);
	                        index += 4;
	                        try {
	                            sb.append((char) Integer.parseInt(hex, 16));
	                        } catch (NumberFormatException e) {
	                            throw new JsonParsingException("Invalid hex in unicode sequence: \\u" + hex);
	                        }
	                    }
	                    default -> throw new JsonParsingException("Invalid escape sequence: \\" + esc);
					}
				} else {
                    sb.append(c);
                }
			}

			throw new JsonParsingException("Unterminated string literal");
		}

		Number parseNumber() {
            int start = index;
            if (peek() == '-') next();

            while (hasMore() && Character.isDigit(peek())) {
                next();
            }

            boolean isFloatingPoint = false;
            if (hasMore() && peek() == '.') {
                isFloatingPoint = true;
                next(); // consume '.'
                while (hasMore() && Character.isDigit(peek())) {
                    next();
                }
            }

            if (hasMore() && (peek() == 'e' || peek() == 'E')) {
                isFloatingPoint = true;
                next();
                if (hasMore() && (peek() == '+' || peek() == '-')) {
                    next();
                }
                while (hasMore() && Character.isDigit(peek())) {
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
                throw new JsonParsingException("Invalid numeric value: " + numStr);
            }
        }

        Boolean parseBoolean() {
            if (src.startsWith("true", index)) {
                index += 4;
                return true;
            } else if (src.startsWith("false", index)) {
                index += 5;
                return false;
            }
            throw new JsonParsingException("Invalid boolean at position " + index);
        }

        Object parseNull() {
            if (src.startsWith("null", index)) {
                index += 4;
                return null;
            }
            throw new JsonParsingException("Invalid null token at position " + index);
        }
	}
}
