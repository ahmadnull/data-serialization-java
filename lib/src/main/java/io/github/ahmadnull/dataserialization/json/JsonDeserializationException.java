package io.github.ahmadnull.dataserialization.json;

public class JsonDeserializationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JsonDeserializationException(String string) {
        super(string);
    }

    public JsonDeserializationException(String string, Exception e) {
        super(string, e);
    }
}
