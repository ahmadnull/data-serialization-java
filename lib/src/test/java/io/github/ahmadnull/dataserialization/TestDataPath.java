package io.github.ahmadnull.dataserialization;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

class TestDataPath {
    @Test void testDataPath() {
    	HashMap<String, Object> map  = new HashMap<String, Object>();
    	map.put("key1", "value1");
    	assertEquals("value1", DataPath.getByPath(map, "key1"));
    	
    	ArrayList<Object> list = new ArrayList<Object>();
    	list.add("value1");
    	list.add("value2");
    	list.add("value3");
    	map.put("list", list);
    	assertEquals("value3", DataPath.getByPath(map, "list[2]"));
    }
}
