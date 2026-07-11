package com.pug523.shelf.gui.renderer.shader;

import java.util.HashMap;
import java.util.Map;

public final class UniformRegistry {
    private static final Map<Integer, UniformApplier> REGISTRY = new HashMap<>();

    public static void put(int i, UniformApplier applier) {
        REGISTRY.put(i, applier);
    }

    public static UniformApplier get(int i) {
        return REGISTRY.get(i);
    }

    public static void remove(int i) {
        REGISTRY.remove(i);
    }

    public static void clear() {
        REGISTRY.clear();
    }
}
