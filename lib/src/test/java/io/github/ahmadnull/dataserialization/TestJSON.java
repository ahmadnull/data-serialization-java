package io.github.ahmadnull.dataserialization;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

class TestJSON {
    @Test void testJSON() {
    	String raw = "{}";
    	HashMap<String, Object> data = JSON.deserialize(raw, HashMap::new);
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
    	    	
    	String serialized = JSON.serialize(data, false);
    	
    	System.out.println(serialized);
    	
    	String expected = "{\"is_alive\": false, \"name\": \"Leibniz\", \"nested_data\": {\"key1\": \"value1\", \"key2\": \"value2\"}, \"birth_year\": 1646, \"nested_array\": [\"item1\", \"item2\"]}";
    	
    	assertEquals(expected, serialized);
    }
}
