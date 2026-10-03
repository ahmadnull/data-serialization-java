package io.github.ahmadnull.dataserialization.json;

public class JsonParsingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JsonParsingException(String string) {
        super(string);
    }

    public JsonParsingException(String string, Exception e) {
        super(string, e);
    }
}
