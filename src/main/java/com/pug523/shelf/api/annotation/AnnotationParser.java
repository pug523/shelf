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

    private static final String TITLE_SUFFIX = ".title";
    private static final String DESCRIPTION_SUFFIX = "_desc";

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
                ConfigEntry entry = ctx.entry();
                ParsingContext activeContext = contextStack.peek();

                String categoryStr = (entry != null) ? entry.category() : "";
                boolean hasCategory = !categoryStr.isEmpty();

                ParsingContext targetContext = activeContext;
                if (hasCategory) {
                    targetContext = categoryCache.get(categoryStr);
                    if (targetContext == null) {
                        ParsingContext[] holder = new ParsingContext[1];
                        String titleKey = categoryStr + TITLE_SUFFIX;

                        activeContext.getBuilder().subCategory(ComponentCompat.translatable(titleKey), b -> holder[0] = new ParsingContext(b));
                        targetContext = holder[0];
                        categoryCache.put(categoryStr, targetContext);
                    }
                }

                String optionKey;
                String groupKey;
                String descKey;

                if (hasCategory) {
                    String localKey = !entry.key().isEmpty() ? entry.key() : ConfigTreeWalker.camelToSnake(ctx.field().getName());
                    optionKey = categoryStr + "." + localKey;

                    String localGroup = !entry.group().isEmpty() ? entry.group() : "default_group";
                    groupKey = categoryStr + "." + localGroup;

                    if (!entry.description().isEmpty()) {
                        descKey = entry.description();
                    } else {
                        descKey = optionKey + DESCRIPTION_SUFFIX;
                    }
                } else {
                    // Absolute path mode fallback tracking system
                    optionKey = (entry != null && !entry.key().isEmpty()) ? entry.key() : ctx.keyPath();
                    groupKey = (entry != null && !entry.group().isEmpty()) ? entry.group() : "all_settings.default_group";

                    if (entry != null && !entry.description().isEmpty()) {
                        descKey = entry.description();
                    } else {
                        descKey = optionKey + DESCRIPTION_SUFFIX;
                    }
                }

                if (ctx.isLeaf()) {
                    OptionWidget<?> widget = createWidgetForField(ctx.field(), ctx.instance(), ctx.defaultInstance(), optionKey, descKey);
                    if (widget != null) {
                        ConfigScreenBuilder.GroupBuilder groupBuilder = targetContext.getGroupCache().get(groupKey);
                        if (groupBuilder == null) {
                            ConfigScreenBuilder.GroupBuilder[] holder = new ConfigScreenBuilder.GroupBuilder[1];
                            targetContext.getBuilder().group(ComponentCompat.translatable(groupKey), b -> holder[0] = b);
                            groupBuilder = holder[0];
                            targetContext.getGroupCache().put(groupKey, groupBuilder);
                        }
                        groupBuilder.add(widget);
                    }
                } else {
                    if (hasCategory) {
                        contextStack.push(targetContext);
                        ctx.recurse();
                        contextStack.pop();
                    } else {
                        String subCategoryTitleKey = optionKey + TITLE_SUFFIX;
                        targetContext.getBuilder().subCategory(ComponentCompat.translatable(subCategoryTitleKey), subBuilder -> {
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

    private static OptionWidget<?> createWidgetForField(Field field, Object instance, Object defaultInstance, String optKey, String descKey) {
        try {
            Option<Object> option = new Option<>(optKey, descKey, field.get(defaultInstance),
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
