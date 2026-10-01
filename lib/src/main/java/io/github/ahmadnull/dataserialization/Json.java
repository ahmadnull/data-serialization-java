package io.github.ahmadnull.dataserialization;

import java.util.List;
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
	}

	public static class JsonValueException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public JsonValueException(Object value) {
			super(value.toString() + ": is not a valid JSON value\n" +
		         "Valid JSON values are: String, Integer, Double, Boolean, Map, List, and null.");
		}
	}

	// Default Values
	static boolean multilineDefault = true;
	static int indentationDefault = 4;
	static int levelDefault = 1;

	/**
	 * Serialize Map&lt;String, Object&gt; into JSON String
	 * @param data
	 * @return JSON String
	 */
	public static String serialize(Object data) {
		return serialize(data, multilineDefault, indentationDefault, levelDefault);
	}

	/**
	 * Serialize Map&lt;String, Object&gt; into JSON String
	 * @param data
	 * @param multiline
	 * @return JSON String
	 */
	public static String serialize(Object data, boolean multiline) {
		return serialize(data, multiline, indentationDefault, levelDefault);
	}

	/**
	 * Serialize Map&lt;String, Object&gt; into JSON String
	 * @param data
	 * @param multiline
	 * @param indentation
	 * @return JSON String
	 */
	public static String serialize(Object data, boolean multiline, int indentation) {
		return serialize(data, multiline, indentation, levelDefault);
	}

	/**
	 * Serialize Map&lt;String, Object&gt; into JSON String
	 * @param data
	 * @param multiline
	 * @param indentation
	 * @param level
	 * @return JSON String
	 */
	public static String serialize(Object data, boolean multiline, int indentation, int level) {
		StringBuilder sb = new StringBuilder();
		serializeValue(data, multiline, indentation, level, sb);
		return sb.toString();
	}

	private static void serializeValue(
			Object value,
			boolean multiline,
			int indentation, int level,
			StringBuilder sb
			) {

		switch(value) {
			case String s -> sb.append('"').append(Helpers.escapeJson(s)).append('"');
			case Integer i -> sb.append(i);
			case Long l -> sb.append(l);
			case Float f -> sb.append(f);
			case Double d -> sb.append(d);
			case Boolean b -> sb.append(b);
			case Map m -> serializeMap(m, multiline, indentation, level, sb);
			case List l -> serializeList(l, multiline, indentation, level, sb);
			case Object[] a -> serializeArray(a, multiline, indentation, level, sb);
			case Object other -> throw new Json.JsonValueException(other);
		}
	}

	public static void serializeMap(
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

	public static void serializeList(
			List<Object> list,
			boolean multiline,
			int indentation, int level,
			StringBuilder sb
			) {

		sb.append('[');

        if (list.iterator().hasNext()) {
            boolean first = true;
            for (Object item : list) {
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

	public static void serializeArray(
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


	/**
	 * Deserialize raw JSON String into Map&lt;String, Object&gt;
	 * @param raw
	 * @return Map&lt;String, Object&gt;
	 * @implNote TODO
	 */
	public static <T extends Map<String, Object>> T deserialize(String raw, Supplier<T> mapFactory) {
		// TODO
		T data = mapFactory.get();
		return data;
	}
}
