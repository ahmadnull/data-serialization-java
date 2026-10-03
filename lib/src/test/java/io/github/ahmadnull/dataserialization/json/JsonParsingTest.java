package io.github.ahmadnull.dataserialization.json;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Runs the JSONTestSuite parsing corpus, see test_suites/README.md.
 *
 * Every file name states the outcome a RFC 8259 compliant parser has to produce,
 * therefore each file is reported as an individual test.
 */
class JsonParsingTest {
    private static final String SUITE_PATH = "/test_parsing/";
    private static final String EXTENSION = ".json";

    // Accepted and rejected files.
    @TestFactory Stream<DynamicNode> mustAccept() {
        return suiteFiles("y_").stream()
            .map(name -> dynamicTest(name, () -> assertDoesNotThrow(
                () -> Json.deserialize(content(name)),
                name + " must be accepted"
            )));
    }

    @TestFactory Stream<DynamicNode> mustReject() {
        return suiteFiles("n_").stream()
            .map(name -> dynamicTest(name, () -> assertThrows(
                JsonParsingException.class,
                () -> Json.deserialize(content(name)),
                name + " must be rejected with a JsonParsingException"
            )));
    }

    // Implementation defined files, the decision is only reported.
    @TestFactory Stream<DynamicNode> implementationDefined() {
        return suiteFiles("i_").stream()
            .map(name -> dynamicTest(name, () -> {
                // Accepting or rejecting is allowed, but a wrong decision must never
                // surface as an unexpected exception such as a NumberFormatException.
                boolean accepted = parse(content(name));
                System.out.printf("%s %s: %s%n", SUITE_PATH, name, accepted ? "accepted" : "rejected");
            }));
    }

    @Test void suiteIsComplete() {
        assertFalse(suiteFiles("y_").isEmpty(), "no accepted files found in " + SUITE_PATH);
        assertFalse(suiteFiles("n_").isEmpty(), "no rejected files found in " + SUITE_PATH);
        assertFalse(suiteFiles("i_").isEmpty(), "no implementation defined files found in " + SUITE_PATH);

        // Every file of the corpus has to be covered by one of the three groups,
        // a file with an unknown prefix would silently never be parsed.
        List<String> unknown = suiteFiles().stream()
            .filter(name -> !name.startsWith("y_") && !name.startsWith("n_") && !name.startsWith("i_"))
            .toList();
        assertTrue(unknown.isEmpty(), "files with an unknown prefix: " + unknown);
    }

    private static boolean parse(String raw) {
        try {
            Json.deserialize(raw);
            return true;
        } catch (JsonParsingException e) {
            return false;
        }
    }

    private static String content(String name) {
        try {
            byte[] bytes = Files.readAllBytes(suiteDirectory().resolve(name));
            // The corpus deliberately contains malformed byte sequences, the String
            // constructor replaces those with the replacement character instead of
            // failing like Files.readString does.
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read test suite file " + name, e);
        }
    }

    private static List<String> suiteFiles() {
        return suiteFiles("");
    }

    private static List<String> suiteFiles(String prefix) {
        try (Stream<Path> files = Files.list(suiteDirectory())) {
            return files
                .map(file -> file.getFileName().toString())
                .filter(name -> name.startsWith(prefix) && name.endsWith(EXTENSION))
                .sorted()
                .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to list test suite files of " + SUITE_PATH, e);
        }
    }

    private static Path suiteDirectory() {
        URL url = JsonParsingTest.class.getResource(SUITE_PATH);
        if (url == null)
            throw new IllegalStateException(
                "The test suites are not on the test classpath, run the tests with ./gradlew test");
        if (!"file".equals(url.getProtocol()))
            throw new IllegalStateException("Expected a directory but got " + url);

        try {
            return Path.of(url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to resolve test suite directory " + url, e);
        }
    }
}
