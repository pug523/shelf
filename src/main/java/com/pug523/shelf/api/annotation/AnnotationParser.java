package com.pug523.shelf.api.annotation;

import com.pug523.shelf.ShelfTextUtil;
import com.pug523.shelf.compat.ComponentCompat;
import com.pug523.shelf.config.ConfigTreeWalker;
import com.pug523.shelf.config.Option;
import com.pug523.shelf.gui.ConfigScreen;
import com.pug523.shelf.api.builder.ConfigScreenBuilder;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.widget.option.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.pug523.shelf.compat.JavaCompat.listOf;

public class AnnotationParser {

    private static class ParsingContext {
        private final ConfigScreenBuilder.CategoryBuilder builder;
        private final Map<String, ConfigScreenBuilder.GroupBuilder> groupCache;

        public ParsingContext(ConfigScreenBuilder.CategoryBuilder builder) {
            this.builder = builder;
            this.groupCache = new HashMap<>();
        }

        public ConfigScreenBuilder.CategoryBuilder getBuilder() {
            return builder;
        }

        public Map<String, ConfigScreenBuilder.GroupBuilder> getGroupCache() {
            return groupCache;
        }
    }

    public static <T> ConfigScreen buildScreen(Component title, Screen parent, T configInstance, T defaultInstance, Runnable onSave, LayoutConfig layoutConfig) {
        ConfigScreenBuilder builder = ConfigScreenBuilder.create(title, parent).onSave(onSave).layout(layoutConfig);

        builder.category(ShelfTextUtil.optText("all_settings"), rootBuilder -> {
            Map<String, ParsingContext> categoryCache = new LinkedHashMap<>();

            Stack<ParsingContext> contextStack = new Stack<>();
            contextStack.push(new ParsingContext(rootBuilder));

            ConfigTreeWalker.walk(configInstance, defaultInstance, ctx -> {
                String groupName = ctx.entry() != null && !ctx.entry().group().isEmpty() ? ctx.entry().group() : "default_group";
                ParsingContext activeContext = contextStack.peek();

                if (ctx.isLeaf()) {
                    OptionWidget<?> widget = createWidgetForField(ctx.field(), ctx.instance(), ctx.defaultInstance(), ctx.keyPath());
                    if (widget != null) {
                        // Safe Map lookup optimization via lambda context
                        ConfigScreenBuilder.GroupBuilder groupBuilder = activeContext.getGroupCache().get(groupName);
                        if (groupBuilder == null) {
                            ConfigScreenBuilder.GroupBuilder[] holder = new ConfigScreenBuilder.GroupBuilder[1];
                            activeContext.getBuilder().group(ComponentCompat.translatable(groupName), b -> holder[0] = b);
                            groupBuilder = holder[0];
                            activeContext.getGroupCache().put(groupName, groupBuilder);
                        }
                        groupBuilder.add(widget);
                    }
                } else {
                    String categoryName = ctx.entry() != null ? ctx.entry().category() : "";

                    if (!categoryName.isEmpty()) {
                        ParsingContext targetCategory = categoryCache.get(categoryName);
                        if (targetCategory == null) {
                            ParsingContext[] holder = new ParsingContext[1];
                            rootBuilder.subCategory(ComponentCompat.translatable(categoryName), b -> holder[0] = new ParsingContext(b));
                            targetCategory = holder[0];
                            categoryCache.put(categoryName, targetCategory);
                        }
                        contextStack.push(targetCategory);
                        ctx.recurse();
                        contextStack.pop();
                    } else {
                        String localKey = ctx.entry() != null && !ctx.entry().key().isEmpty() ? ctx.entry().key() : ConfigTreeWalker.camelToSnake(ctx.field().getName());
                        activeContext.getBuilder().subCategory(ComponentCompat.translatable(localKey), subBuilder -> {
                            contextStack.push(new ParsingContext(subBuilder));
                            ctx.recurse();
                            contextStack.pop();
                        });
                    }
                }
            });
        });

        return builder.build();
    }

    public static <T> ConfigScreen buildScreen(
        Component title,
        Screen parent,
        T configInstance,
        T defaultInstance,
        Runnable onSave) {
        return buildScreen(title, parent, configInstance, defaultInstance, onSave, LayoutConfig.createDefault());
    }

    private static OptionWidget<?> createWidgetForField(Field field, Object instance, Object defaultInstance, String optKey) {
        try {
            Option<Object> option = new Option<>(optKey, field.get(defaultInstance),
                () -> { try { return field.get(instance); } catch (Exception e) { throw new RuntimeException(e); } },
                val -> { try { field.set(instance, val); } catch (Exception e) { throw new RuntimeException(e); } },
                listOf()
            );
            return WidgetRegistry.createWidget(field, option);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> OptionWidget<List<T>> createListWidgetForField(Field field, GuiOption<List<T>> option) {
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
            Function<GuiOption<T>, OptionWidget<T>> itemWidgetFactory = (tempOpt) ->
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
}
