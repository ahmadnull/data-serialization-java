package io.github.ahmadnull.dataserialization;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashMap;

class TestJson {
    @Test void testJson() {
    	String raw = "{}";
    	HashMap<String, Object> data = Json.deserialize(raw, HashMap::new);
    	data.put("name", "Leibniz");
    	data.put("is_alive", false);
    	data.put("birth_year", 1646);

    	HashMap<String, Object> nestedData = new HashMap<String, Object>();
    	nestedData.put("key1", "value1");
    	nestedData.put("key2", "value2");
    	data.put("nested_data", nestedData);

    	ArrayList<Object> nestedList = new ArrayList<Object>();
    	nestedList.add("item1");
    	nestedList.add("item2");
    	data.put("nested_list", nestedList);

    	Object[] nestedArray = {1, 2, 3};
    	data.put("nested_array", nestedArray);

    	String serialized = Json.serialize(data, false);
    	System.out.println(serialized);
    	String expected = "{\"is_alive\": false, \"name\": \"Leibniz\", \"nested_data\": {\"key1\": \"value1\", \"key2\": \"value2\"}, \"nested_list\": [\"item1\", \"item2\"], \"birth_year\": 1646, \"nested_array\": [1, 2, 3]}";

    	assertEquals(expected, serialized);
    }

	private class InvalidType { }

    @Test void testJsonValueException() {
    	HashMap<String, Object> data = new HashMap<String, Object>();
    	data.put("invalid_type", new InvalidType());

    	assertThrows(Json.JsonValueException.class, () -> Json.serialize(data));
    }
}
