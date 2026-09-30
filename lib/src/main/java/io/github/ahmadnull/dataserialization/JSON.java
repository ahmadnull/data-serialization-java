package io.github.ahmadnull.dataserialization;

import java.util.HashMap;

public class JSON {
	
	static boolean multilineDefault = true;
	static int indentationDefault = 4;
	static int levelDefault = 1;
	
	/**
	 * Serialize HashMap<String, Object> into JSON String
	 * @param data
	 * @param multiline
	 * @param indentation
	 * @param level
	 * @return JSON String
	 */
	public static String serialize(HashMap<String, Object> data, boolean multiline, int indentation, int level) {
		String serialized = Utils.repeatSpace(indentation * (level - 1)) + "{";

		for (HashMap.Entry<String, Object> entry : data.entrySet()) {
			if (multiline) 
				serialized += '\n' + Utils.repeatSpace(indentation * level);
			
			serialized += '"' + entry.getKey().toString() + '"' + ':';
			
			switch(entry.getValue()) {
			case String s -> serialized += '"' + s + '"';
			case Integer i -> serialized += '"' + i.toString() + '"';
			case Double d -> serialized += '"' + d.toString() + '"';
			case Boolean b -> serialized += "" + b;
			case null -> serialized += "null";
			default -> System.out.println("Invalid value");
			}
			
			serialized += ',';
			
			if (!multiline)
				serialized += ' ';
		}
		
		// Remove the trailing comma ','
		serialized = serialized.substring(0, serialized.length() - 1);
		
		if (multiline)
			serialized += '\n';
		
		serialized += Utils.repeatSpace(indentation * (level - 1));
		
			serialized += '}';
		
		return serialized;
	}
	
	public static String serialize(HashMap<String, Object> data, boolean multiline, int indentation) {
		return serialize(data, multiline, indentation, levelDefault);
	}
	
	public static String serialize(HashMap<String, Object> data, boolean multiline) {
		return serialize(data, multiline, indentationDefault, levelDefault);
	}
	
	public static String serialize(HashMap<String, Object> data) {
		return serialize(data, multilineDefault, indentationDefault, levelDefault);
	}
	
	public static HashMap<String, Object> deserialize(String raw) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		return data;
	}
}
