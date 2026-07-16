package com.pug523.shelf.api.annotation;

import com.pug523.shelf.ShelfTextUtil;
import com.pug523.shelf.compat.ComponentCompat;
import com.pug523.shelf.config.Option;
import com.pug523.shelf.gui.ConfigScreen;
import com.pug523.shelf.api.builder.ConfigScreenBuilder;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.widget.option.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.lang.annotation.Annotation;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.pug523.shelf.compat.JavaCompat.listOf;

public class AnnotationParser {

    public static <T> ConfigScreen buildScreen(
        Component title,
        Screen parent,
        T configInstance,
        T defaultInstance,
        Runnable onSave,
        LayoutConfig layoutConfig
    ) {
        ConfigScreenBuilder builder = ConfigScreenBuilder.create(title, parent)
            .onSave(onSave)
            .layout(layoutConfig);

        builder.category(ShelfTextUtil.optText("all_settings"), rootBuilder -> {
            Map<String, ConfigScreenBuilder.CategoryBuilder> categoryCache = new LinkedHashMap<>();

            parseRecursive(
                configInstance,
                defaultInstance,
                "",
                rootBuilder,
                categoryCache,
                "default_group",
                rootBuilder
            );
        });

        return builder.build();
    }

    private static void parseRecursive(
        Object instance,
        Object defaultInstance,
        String keyPrefix,
        ConfigScreenBuilder.CategoryBuilder currentContext,
        Map<String, ConfigScreenBuilder.CategoryBuilder> categoryCache,
        String currentGroup,
        ConfigScreenBuilder.CategoryBuilder rootBuilder
    ) {
        if (instance == null || defaultInstance == null) return;

        Class<?> clazz = instance.getClass();

        Map<String, List<OptionWidget<?>>> groupWidgets = new LinkedHashMap<>();
        List<Field> subObjects = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);

            Class<?> type = field.getType();
            ConfigEntry entry = field.getAnnotation(ConfigEntry.class);

            String localKey = entry != null && !entry.key().isEmpty()
                ? entry.key()
                : camelToSnake(field.getName());

            String optKey = keyPrefix.isEmpty() ? localKey : keyPrefix + "." + localKey;
            String groupName = entry != null && !entry.group().isEmpty() ? entry.group() : currentGroup;

            if (isLeafType(type)) {
                OptionWidget<?> widget = createWidgetForField(field, instance, defaultInstance, optKey);
                if (widget != null) {
                    groupWidgets.computeIfAbsent(groupName, k -> new ArrayList<>()).add(widget);
                }
            } else {
                subObjects.add(field);
            }
        }

        for (Map.Entry<String, List<OptionWidget<?>>> entry : groupWidgets.entrySet()) {
            Component groupTitle = ComponentCompat.translatable(entry.getKey());
            currentContext.group(groupTitle, groupBuilder -> {
                groupBuilder.addAll(entry.getValue());
            });
        }

        for (Field field : subObjects) {
            try {
                Object childInstance = field.get(instance);
                Object childDefaultInstance = field.get(defaultInstance);

                if (childInstance != null && childDefaultInstance != null) {
                    ConfigEntry entry = field.getAnnotation(ConfigEntry.class);
                    String localKey = entry != null && !entry.key().isEmpty() ? entry.key() : camelToSnake(field.getName());
                    String optKey = keyPrefix.isEmpty() ? localKey : keyPrefix + "." + localKey;

                    String categoryName = entry != null ? entry.category() : "";
                    String groupName = entry != null && !entry.group().isEmpty() ? entry.group() : currentGroup;

                    if (!categoryName.isEmpty()) {
                        ConfigScreenBuilder.CategoryBuilder targetCategory = categoryCache.computeIfAbsent(categoryName, name -> {
                            ConfigScreenBuilder.CategoryBuilder[] holder = new ConfigScreenBuilder.CategoryBuilder[1];
                            rootBuilder.subCategory(ComponentCompat.translatable(name), b -> {
                                holder[0] = b;
                            });
                            return holder[0];
                        });

                        parseRecursive(childInstance, childDefaultInstance, optKey, targetCategory, categoryCache, groupName, rootBuilder);
                    } else {
                        Component subCategoryTitle = ComponentCompat.translatable(localKey);
                        currentContext.subCategory(subCategoryTitle, subBuilder -> {
                            parseRecursive(childInstance, childDefaultInstance, optKey, subBuilder, categoryCache, groupName, rootBuilder);
                        });
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static OptionWidget<?> createWidgetForField(
        Field field,
        Object instance,
        Object defaultInstance,
        String optKey
    ) {
        try {
            Object defaultValue = field.get(defaultInstance);
            Option<Object> option = new Option<>(
                optKey,
                defaultValue,
                () -> {
                    try {
                        return field.get(instance);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                val -> {
                    try {
                        field.set(instance, val);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                // TODO: tags
                listOf()
            );

            OptionWidget<?> widget = WidgetRegistry.createWidget(field, option);
            if (widget != null) {
                return widget;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> OptionWidget<List<T>> createListWidgetForField(Field field, Option<List<T>> option) {
        try {
            WidgetTypes.List listAnno = field.getAnnotation(WidgetTypes.List.class);
            if (listAnno == null) return null;

            ParameterizedType listType = (ParameterizedType) field.getGenericType();
            Class<T> elementType = (Class<T>) listType.getActualTypeArguments()[0];

            Class<? extends Annotation> childAnnoClass = listAnno.value();
            Annotation childAnno = null;
            if (childAnnoClass != Annotation.class) {
                childAnno = field.getAnnotation(childAnnoClass);
            }

            final Annotation finalChildAnno = childAnno;
            Function<Option<T>, OptionWidget<T>> itemWidgetFactory = (tempOpt) ->
                WidgetRegistry.createWidgetForType(elementType, finalChildAnno, tempOpt);

            Supplier<T> defaultValueSupplier = () -> {
                if (listAnno.creator() != WidgetTypes.List.DefaultCreator.class) {
                    try {
                        Supplier<T> customCreator = (Supplier<T>) listAnno.creator().getDeclaredConstructor().newInstance();
                        return customCreator.get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return (T) createDefaultValueForType(elementType);
            };

            return new ListOptionWidget<>(option, itemWidgetFactory, defaultValueSupplier);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object createDefaultValueForType(Class<?> type) {
        if (type == boolean.class || type == Boolean.class) return Boolean.FALSE;
        if (type == int.class || type == Integer.class) return 0;
        if (type == double.class || type == Double.class) return 0.0d;
        if (type == float.class || type == Float.class) return 0.0f;
        if (type == String.class) return "";
        if (type.isEnum()) return type.getEnumConstants()[0];
        if (Item.class.isAssignableFrom(type)) return net.minecraft.world.item.Items.AIR;
        return null;
    }

    private static boolean isLeafType(Class<?> type) {
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

    private static String camelToSnake(String str) {
        if (str == null || str.isEmpty()) return "";
        return str.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();
    }
}
