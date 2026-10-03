package io.github.ahmadnull.dataserialization.json;

public class JsonSerializationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JsonSerializationException(String message) {
        super(message);
    }

    public JsonSerializationException(Object value) {
        super(value.toString() + ": is not a valid JSON value\n" +
            "Valid JSON values are: String, Integer, Double, Boolean, Map, Collection, and null.");
    }
}
