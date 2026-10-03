package io.github.ahmadnull.dataserialization.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;

import org.junit.jupiter.api.Test;

class JsonSettingsTest {
    @Test void jsonSettings() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");

        String serialized = Json.serialize(map, new JsonSettings().multiline(false));
        System.out.println(serialized);

        String expected = "{\"key1\": \"value1\", \"key2\": \"value2\", \"key3\": \"value3\"}";
        assertEquals(expected, serialized);
    }
}
