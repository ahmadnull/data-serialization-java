package io.github.ahmadnull.dataserialization.toml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TomlSettings {
    // Default Values
    private static final Supplier<Map<String, Object>> mapFactoryDefault = LinkedHashMap::new;
    private static final Supplier<Collection<Object>> collectionFactoryDefault = ArrayList::new;
    private static final int maxDepthDefault = 1000;

    private Supplier<Map<String, Object>> mapFactory;
    private Supplier<Collection<Object>> collectionFactory;
    private int maxDepth;

    public TomlSettings() {
        this.mapFactory = mapFactoryDefault;
        this.collectionFactory = collectionFactoryDefault;
        this.maxDepth = maxDepthDefault;
    }

    /**
     * An independent copy of these settings, to vary one value without
     * disturbing the original.
     *
     * <p>The two factories are shared by reference on purpose, a supplier is a
     * stateless factory and copying the reference is what the caller means.
     */
    public TomlSettings copy() {
        TomlSettings copy = new TomlSettings();
        copy.mapFactory = this.mapFactory;
        copy.collectionFactory = this.collectionFactory;
        copy.maxDepth = this.maxDepth;
        return copy;
    }

    // --- Setters ---

    public TomlSettings mapFactory(Supplier<Map<String, Object>> mapFactory) {
        this.mapFactory = mapFactory;
        return this;
    }

    public TomlSettings collectionFactory(Supplier<Collection<Object>> collectionFactory) {
        this.collectionFactory = collectionFactory;
        return this;
    }

    public TomlSettings maxDepth(int maxDepth) {
        if (maxDepth < 0)
            throw new IllegalArgumentException("maxDepth must not be negative: " + maxDepth);
        this.maxDepth = maxDepth;
        return this;
    }

    // --- Getters ---

    public Supplier<Map<String, Object>> mapFactory() {
        return this.mapFactory;
    }

    public Supplier<Collection<Object>> collectionFactory() {
        return this.collectionFactory;
    }

    public int maxDepth() {
        return this.maxDepth;
    }
}
