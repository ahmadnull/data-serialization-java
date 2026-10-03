package io.github.ahmadnull.dataserialization.toml;

public class TomlDeserializationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TomlDeserializationException(String string) {
        super(string);
    }

    public TomlDeserializationException(String string, Exception e) {
        super(string, e);
    }
}
