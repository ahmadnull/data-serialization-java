package io.github.ahmadnull.dataserialization;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

class FormatTest {
    @Test void testJSONFormat() {
    	String raw = "raw";
    	HashMap<String, Object> data = JSON.deserialize(raw);
    	data.put("name", "Leibniz");
    	data.put("is_alive", false);
    	data.put("birth_year", 1646);
    	String serialized = JSON.serialize(data, true, 2);
    	
    	System.out.println("Hello World from Ahmad!");
    	System.out.println(serialized);
    	
    	//assertEquals("", serialized);
    }
}
