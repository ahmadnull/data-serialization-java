package io.github.ahmadnull.dataserialization;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Toml {
	
	/**
	 * Serialize HashMap&lt;String, Object&gt; into TOML String
	 * @param data
	 * @return TOML String
	 */
	public static String serialize(Map<String, Object> data) {
		String serialized = "";
		
		for (Map.Entry<String, Object> entry : data.entrySet()) {
			switch(entry.getValue()) {
				case String s -> serialized += entry.getKey() + " = " + '"' + s + '"';
				case Integer i -> serialized += entry.getKey() + " = " + i.toString();
				case Double d -> serialized += entry.getKey() + " = " + d.toString();
				case Boolean b -> serialized += entry.getKey() + " = " + b.toString();
				case Map h -> serialized += "\n[" + entry.getKey() + ']' + '\n' + serialize(h);
				case List a -> serialized += entry.getKey() + " = "  + serialize(a);
				case null -> serialized += "null";
				default -> System.out.println("Invalid value");
			}
			
			serialized += '\n';
		}
		
		return serialized;
	}
	
	/**
	 * Serialize ArrayList&lt;Object&gt; into partial TOML String (Not intended for public use)
	 * @param array
	 * @return Partial TOML String
	 */
	private static String serialize(List<Object> array) {
		String serialized = "[ ";
		
		for (Object item : array) {
			switch(item) {
				case String s -> serialized += '"' + s + '"';
				case Integer i -> serialized += i.toString();
				case Double d -> serialized += d.toString();
				case Boolean b -> serialized += "" + b;
				//case HashMap h -> serialized += serialize(h);
				case List a -> serialized += serialize(a);
				case null -> serialized += "null";
				default -> System.out.println("Invalid value");
			}
			
			serialized += ", ";
		}
		
		// Remove the trailing comma ','
		int trailing = 2;
		serialized = serialized.substring(0, serialized.length() - trailing);
		
		serialized += " ]";
		
		return serialized;
	}
	
	/**
	 * Deserialize raw TOML String into Map&lt;String, Object&gt;
	 * @param <T>
	 * @param raw
	 * @param mapFactory
	 * @return Map&lt;String, Object&gt;
	 */
	public static <T extends Map<String, Object>> T deserialize(String raw, Supplier<T> mapFactory) {
		// TODO
		T data = mapFactory.get();
		return data;
	}
}
