package io.github.ahmadnull.dataserialization;

import java.util.ArrayList;
import java.util.HashMap;

public class TOML {
	
	/**
	 * Serialize HashMap&lt;String, Object&gt; into TOML String
	 * @param data
	 * @return TOML String
	 */
	public static String serialize(HashMap<String, Object> data) {
		String serialized = "";
		
		for (HashMap.Entry<String, Object> entry : data.entrySet()) {
			switch(entry.getValue()) {
				case String s -> serialized += entry.getKey() + " = " + '"' + s + '"';
				case Integer i -> serialized += entry.getKey() + " = " + i.toString();
				case Double d -> serialized += entry.getKey() + " = " + d.toString();
				case Boolean b -> serialized += entry.getKey() + " = " + b.toString();
				case HashMap h -> serialized += "\n[" + entry.getKey() + ']' + '\n' + serialize(h);
				case ArrayList a -> serialized += entry.getKey() + " = "  + serialize(a);
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
	private static String serialize(ArrayList<Object> array) {
		String serialized = "[ ";
		
		for (Object item : array) {
			switch(item) {
				case String s -> serialized += '"' + s + '"';
				case Integer i -> serialized += i.toString();
				case Double d -> serialized += d.toString();
				case Boolean b -> serialized += "" + b;
				//case HashMap h -> serialized += serialize(h);
				case ArrayList a -> serialized += serialize(a);
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
	 * Deserialize raw TOML String into HashMap&lt;String, Object&gt;
	 * @param raw
	 * @return HashMap&lt;String, Object&gt;
	 * @implNote TODO
	 */
	public static HashMap<String, Object> deserialize(String raw) {
		// TODO
		HashMap<String, Object> data = new HashMap<String, Object>();
		return data;
	}
}
