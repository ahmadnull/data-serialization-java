package io.github.ahmadnull.dataserialization;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

class FormatTest {
    @Test void testJSONFormat() {
    	String raw = "raw";
    	HashMap<Object, Object> data = Format.JSON.deserialize(raw);
    	String serialized = Format.JSON.serialize(data);
    	
    	assertEquals("", serialized);
    }
}
