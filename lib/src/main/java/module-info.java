/**
 * Static data serialization for JSON, TOML and other self describing formats.
 *
 * <p>The exported packages are the whole public API. Everything else, most
 * notably {@code io.github.ahmadnull.dataserialization.internal}, stays
 * unexported so that helpers can be shared between the format packages without
 * being reachable for consumers of the library.
 */
module io.github.ahmadnull.dataserialization {
    // Format agnostic queries over the parsed data model.
    exports io.github.ahmadnull.dataserialization;

    // One package per format, added as the format is implemented.
    exports io.github.ahmadnull.dataserialization.json;
    exports io.github.ahmadnull.dataserialization.toml;

    // io.github.ahmadnull.dataserialization.internal is intentionally omitted,
    // see the module documentation above.
}
