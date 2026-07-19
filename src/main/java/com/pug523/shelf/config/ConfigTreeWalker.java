package com.pug523.shelf.config;

import com.pug523.shelf.api.annotation.ConfigEntry;
import net.minecraft.world.item.Item;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class ConfigTreeWalker {
    @FunctionalInterface
    public interface Visitor {
        void visitNode(FieldContext context);
    }

    public static void walk(Object instance, Object defaultInstance, Visitor visitor) {
        walkRecursive(instance, defaultInstance, "", visitor);
    }

    private static void walkRecursive(Object instance, Object defaultInstance, String parentKeyPath, Visitor visitor) {
        if (instance == null || defaultInstance == null) return;

        for (Field field : instance.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);

            ConfigEntry entry = field.getAnnotation(ConfigEntry.class);
            String localKey = entry != null && !entry.key().isEmpty() ? entry.key() : camelToSnake(field.getName());
            String currentPath = parentKeyPath.isEmpty() ? localKey : parentKeyPath + "." + localKey;

            Class<?> type = field.getType();
            boolean leaf = isLeafType(type);

            FieldContext context = new ContextImpl(field, type, instance, defaultInstance, currentPath, entry, leaf,
                () -> {
                    try {
                        Object childInstance = field.get(instance);
                        Object childDefault = field.get(defaultInstance);
                        walkRecursive(childInstance, childDefault, currentPath, visitor);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            );

            visitor.visitNode(context);
        }
    }

    public static boolean isLeafType(Class<?> type) {
        return type.isPrimitive()
            || type == Boolean.class
            || type == Integer.class
            || type == Float.class
            || type == Double.class
            || type == String.class
            || type.isEnum()
            || Item.class.isAssignableFrom(type)
            || List.class.isAssignableFrom(type);
    }

    public static String camelToSnake(String str) {
        if (str == null || str.isEmpty()) return "";
        return str.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();
    }

    /// Context record/interface keeping data read-only and clean
    public interface FieldContext {
        Field field();

        Class<?> type();

        Object instance();

        Object defaultInstance();

        String keyPath();

        ConfigEntry entry();

        boolean isLeaf();

        void recurse();
    }

    private record ContextImpl(Field field, Class<?> type, Object instance, Object defaultInstance,
                               String keyPath, ConfigEntry entry, boolean isLeaf,
                               Runnable recurseRunnable) implements FieldContext {
        @Override
        public void recurse() {
            recurseRunnable.run();
        }
    }
}
