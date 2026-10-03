package io.github.ahmadnull.dataserialization.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class JsonSettings {
    // Default Values
    private static final boolean multilineDefault = true;
    private static final int indentationDefault = 4;
    private static final int levelDefault = 1;
    private static final Supplier<Map<String, Object>> mapFactoryDefault = LinkedHashMap::new;
    private static final Supplier<Collection<Object>> collectionFactoryDefault = ArrayList::new;
    private static final int maxDepthDefault = 1000;

    private boolean multiline;
    private int indentation;
    private int level;
    private Supplier<Map<String, Object>> mapFactory;
    private Supplier<Collection<Object>> collectionFactory;
    private int maxDepth;

    public JsonSettings() {
        this.multiline = multilineDefault;
        this.indentation = indentationDefault;
        this.level = levelDefault;
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
    public JsonSettings copy() {
        JsonSettings copy = new JsonSettings();
        copy.multiline = this.multiline;
        copy.indentation = this.indentation;
        copy.level = this.level;
        copy.mapFactory = this.mapFactory;
        copy.collectionFactory = this.collectionFactory;
        copy.maxDepth = this.maxDepth;
        return copy;
    }

    // --- Setters ---

    public JsonSettings multiline(boolean multiline) {
        this.multiline = multiline;
        return this;
    }

    public JsonSettings indentation(int indentation) {
        if (indentation < 0)
            throw new IllegalArgumentException("indentation must not be negative: " + indentation);
        this.indentation = indentation;
        return this;
    }

    public JsonSettings level(int level) {
        if (level < 0)
            throw new IllegalArgumentException("level must not be negative: " + level);
        this.level = level;
        return this;
    }

    public JsonSettings mapFactory(Supplier<Map<String, Object>> mapFactory) {
        this.mapFactory = mapFactory;
        return this;
    }

    public JsonSettings collectionFactory(Supplier<Collection<Object>> collectionFactory) {
        this.collectionFactory = collectionFactory;
        return this;
    }

    public JsonSettings maxDepth(int maxDepth) {
        if (maxDepth < 0)
            throw new IllegalArgumentException("maxDepth must not be negative: " + maxDepth);
        this.maxDepth = maxDepth;
        return this;
    }

    // --- Getters ---

    public boolean multiline() {
        return this.multiline;
    }

    public int indentation() {
        return this.indentation;
    }

    public int level() {
        return this.level;
    }

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
