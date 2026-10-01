package io.github.ahmadnull.dataserialization;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class JSON {
	
	// Default Values
	static boolean multilineDefault = true;
	static int indentationDefault = 4;
	static int levelDefault = 1;
	
	/**
	 * Serialize HashMap&lt;String, Object&gt; into JSON String
	 * @param data
	 * @param multiline
	 * @param indentation
	 * @param level
	 * @return JSON String
	 */
	public static String serialize(Map<String, Object> data, boolean multiline, int indentation, int level) {
		String serialized = "{";

		for (Map.Entry<String, Object> entry : data.entrySet()) {
			if (multiline) 
				serialized += '\n' + Utils.repeatSpace(indentation * level);
			
			serialized += '"' + entry.getKey().toString() + '"' + ':' + ' ';
			
			switch(entry.getValue()) {
				case String s -> serialized += '"' + s + '"';
				case Integer i -> serialized += i.toString();
				case Double d -> serialized += d.toString();
				case Boolean b -> serialized += b.toString();
				case Map h -> serialized += serialize(h, multiline, indentation, level + 1);
				case List a -> serialized += serialize(a, multiline, indentation, level + 1);
				case null -> serialized += "null";
				default -> System.out.println("Invalid value");
			}
			
			serialized += ',';
			
			if (!multiline)
				serialized += ' ';
		}
		
		// Remove the trailing comma ','
		int trailing = multiline ? 1 : 2;
		serialized = serialized.substring(0, serialized.length() - trailing);
		
		if (multiline)
			serialized += '\n' + Utils.repeatSpace(indentation * (level - 1));
		
		serialized += '}';
		
		return serialized;
	}
	
	/**
	 * Serialize HashMap&lt;String, Object&gt; into JSON String
	 * @param data
	 * @param multiline
	 * @param indentation
	 * @return JSON String
	 */
	public static String serialize(Map<String, Object> data, boolean multiline, int indentation) {
		return serialize(data, multiline, indentation, levelDefault);
	}
	
	/**
	 * Serialize HashMap&lt;String, Object&gt; into JSON String
	 * @param data
	 * @param multiline
	 * @return JSON String
	 */
	public static String serialize(Map<String, Object> data, boolean multiline) {
		return serialize(data, multiline, indentationDefault, levelDefault);
	}
	
	/**
	 * Serialize HashMap&lt;String, Object&gt; into JSON String
	 * @param data
	 * @return JSON String
	 */
	public static String serialize(Map<String, Object> data) {
		return serialize(data, multilineDefault, indentationDefault, levelDefault);
	}
	
	/**
	 * Serialize ArrayList&lt;Object&gt; into partial JSON String (Not intended for public use)
	 * @param array
	 * @param multiline
	 * @param indentation
	 * @param level
	 * @return Partial JSON String
	 */
	private static String serialize(List<Object> array, boolean multiline, int indentation, int level) {
		String serialized = "[";
		
		for (Object item : array) {
			if (multiline) 
				serialized += '\n' + Utils.repeatSpace(indentation * level);

			switch(item) {
				case String s -> serialized += '"' + s + '"';
				case Integer i -> serialized += i.toString() + '"';
				case Double d -> serialized += d.toString();
				case Boolean b -> serialized += b.toString();
				case Map h -> serialized += serialize(h, multiline, indentation, level + 1);
				case List a -> serialized += serialize(a, multiline, indentation, level + 1);
				case null -> serialized += "null";
				default -> System.out.println("Invalid value");
			}
			
			serialized += ',';
			
			if (!multiline)
				serialized += ' ';
		}
		
		// Remove the trailing comma ','
		int trailing = multiline ? 1 : 2;
		serialized = serialized.substring(0, serialized.length() - trailing);
		
		if (multiline)
			serialized += '\n' + Utils.repeatSpace(indentation * (level - 1));
		
		serialized += ']';
		
		return serialized;
	}
	
	/**
	 * Deserialize raw JSON String into HashMap&lt;String, Object&gt;
	 * @param raw
	 * @return HashMap&lt;String, Object&gt;
	 * @implNote TODO
	 */
	public static <T extends Map<String, Object>> T deserialize(String raw, Supplier<T> mapFactory) {
		// TODO
		T data = mapFactory.get();
		return data;
	}
}
