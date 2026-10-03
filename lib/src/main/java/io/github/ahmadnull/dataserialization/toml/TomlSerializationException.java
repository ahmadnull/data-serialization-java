package io.github.ahmadnull.dataserialization.toml;

public class TomlSerializationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TomlSerializationException(String message) {
        super(message);
    }

    public TomlSerializationException(Object value) {
        super(value.toString() + ": is not a valid TOML value\n" +
            "Valid TOML values are: String, Number, Boolean, Map, Collection, Temporal, and null.");
    }

    public static TomlSerializationException ofNull() {
        return new TomlSerializationException("TOML doesn't accept null as a value");
    }
}
