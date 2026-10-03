package io.github.ahmadnull.dataserialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.module.ModuleDescriptor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The public API is the {@code exports} list of the module descriptor.
 *
 * <p>This test pins that list so that a package cannot be exported by accident
 * and so that the set stays reviewable in one place instead of being spread over
 * the {@code module-info.java} diff of every release.
 */
class ModuleApiSurfaceTest {
    private static final String MODULE_NAME = "io.github.ahmadnull.dataserialization";

    /** Anchors the descriptor lookup, see {@link #descriptor()}. */
    private static final Class<?> ANCHOR = io.github.ahmadnull.dataserialization.json.Json.class;

    private static final String INTERNAL = MODULE_NAME + ".internal";

    /** Every package a consumer of the library is allowed to import. */
    private static final Set<String> PUBLIC_API = Set.of(
        "io.github.ahmadnull.dataserialization",
        "io.github.ahmadnull.dataserialization.json",
        "io.github.ahmadnull.dataserialization.toml");

    @Test void exportsExactlyThePublicApi() {
        assertEquals(PUBLIC_API, exportedPackages(descriptor()));
    }

    /**
     * A package only appears in the descriptor once it exports or opens
     * something, and that is the one thing the internal package must never do.
     * The check therefore reads the compiled classes off the main output, since
     * a package that is entirely unexported is legitimately invisible to the
     * descriptor.
     */
    @Test void containsTheInternalPackage() {
        Path root = Path.of(ANCHOR.getProtectionDomain().getCodeSource().getLocation().getPath())
            .resolve(INTERNAL.replace('.', '/'));

        assertTrue(
            Files.isDirectory(root),
            "the internal package has to exist and hold the shared helpers, expected at " + root);

        try (Stream<Path> classes = Files.list(root)) {
            assertTrue(
                classes.anyMatch(name -> name.getFileName().toString().endsWith(".class")),
                "the internal package is empty, expected shared helpers in " + root);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to list " + root, e);
        }
    }

    @Test void doesNotExportTheInternalPackage() {
        assertFalse(
            exportedPackages(descriptor()).contains(INTERNAL),
            INTERNAL + " must stay unexported, see module-info.java");
    }

    /**
     * Nothing in this library has to be reflectively opened, it only ever
     * reflects over the model classes of its callers. An {@code opens} entry is
     * therefore a wider surface than the library needs.
     */
    @Test void doesNotOpenAnyPackage() {
        Set<String> opened = descriptor().opens().stream()
            .map(ModuleDescriptor.Opens::source)
            .collect(Collectors.toCollection(TreeSet::new));

        assertEquals(Set.of(), opened, "no package needs to be open for deep reflection");
    }

    /**
     * The library only uses the JDK, so every extra dependency is a decision
     * that has to show up in a review of this test.
     */
    @Test void requiresNothingBeyondJavaBase() {
        assertEquals(
            Set.of(),
            descriptor().requires().stream()
                .filter(requirement -> !requirement.name().equals("java.base"))
                .collect(Collectors.toCollection(TreeSet::new)),
            "the library is dependency free, see lib/build.gradle.kts");
    }

    private static Set<String> exportedPackages(ModuleDescriptor descriptor) {
        return descriptor.exports().stream()
            .map(ModuleDescriptor.Exports::source)
            .collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * The descriptor compiled from {@code module-info.java}.
     *
     * <p>It is read back out of the class file rather than taken from the running
     * module, because the test task executes on the classpath. Gradle only
     * infers the module path for tests when the main runtime is a jar, and the
     * test task consumes the class directory instead, so the tests would end up
     * in the unnamed module with no descriptor to inspect. Reading the class
     * file asserts the same thing, keeps working for a classpath only run, and
     * checks the descriptor that actually ships in the jar.
     *
     * <p>The lookup is anchored to the code source of a library class, which
     * resolves to the main class directory. A bare
     * {@code "/module-info.class"} lookup would return whichever one comes first
     * on the classpath, and a modular dependency has one of its own.
     */
    private static ModuleDescriptor descriptor() {
        Path moduleInfo = Path.of(ANCHOR.getProtectionDomain().getCodeSource().getLocation().getPath())
            .resolve("module-info.class");

        assertTrue(
            Files.isRegularFile(moduleInfo),
            "module-info.class is missing next to the compiled library, expected at " + moduleInfo);

        try (InputStream stream = Files.newInputStream(moduleInfo)) {
            ModuleDescriptor descriptor = ModuleDescriptor.read(stream);

            assertEquals(MODULE_NAME, descriptor.name(), "the descriptor has to describe " + MODULE_NAME);

            return descriptor;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read " + moduleInfo, e);
        }
    }
}
