package io.github.ahmadnull.dataserialization;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

class TomlTest {
    @Test void testToml() {
    	String raw = "";
    	HashMap<String, Object> data = Toml.deserialize(raw, HashMap::new);
    	data.put("name", "Leibniz");
    	data.put("is_alive", false);
    	data.put("birth_year", 1646);

    	HashMap<String, Object> nestedData = new HashMap<String, Object>();
    	nestedData.put("key1", "value1");
    	nestedData.put("key2", "value2");

    	data.put("nested_data", nestedData);

    	ArrayList<Object> nestedArray = new ArrayList<Object>();
    	nestedArray.add("item1");
    	nestedArray.add("item2");

    	data.put("nested_array", nestedArray);

    	String serialized = Toml.serialize(data);

    	System.out.println(serialized);

    	String expected =
    	  "is_alive = false\n"
    	+ "name = \"Leibniz\"\n"
    	+ "\n"
    	+ "[nested_data]\n"
    	+ "key1 = \"value1\"\n"
    	+ "key2 = \"value2\"\n"
    	+ "\n"
    	+ "birth_year = 1646\n"
    	+ "nested_array = [ \"item1\", \"item2\" ]"
    	+ "\n";

    	assertEquals(expected, serialized);
    }
}
