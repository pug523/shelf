package com.pug523.shelf.api.annotation;

import com.mojang.blaze3d.platform.InputConstants;
import com.pug523.shelf.compat.ComponentCompat;
import com.pug523.shelf.config.Option;
import com.pug523.shelf.gui.widget.option.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
public class WidgetRegistry {
    private static final List<WidgetFactory> FACTORIES = new ArrayList<>();

    public static void register(WidgetFactory factory) {
        FACTORIES.add(0, factory);
    }

    public static OptionWidget<?> createWidget(Field field, Option<Object> option) {
        for (WidgetFactory factory : FACTORIES) {
            OptionWidget<?> widget = factory.create(field, option);
            if (widget != null) {
                return widget;
            }
        }
        return null;
    }

    public static <E> OptionWidget<E> createWidgetForType(
        Class<E> type,
        Annotation overrideAnnotation,
        GuiOption<E> option
    ) {
        if (type == boolean.class || type == Boolean.class) {
            GuiOption<Boolean> boolOpt = (GuiOption<Boolean>) option;
            if (overrideAnnotation instanceof WidgetTypes.Toggle) {
                WidgetTypes.Toggle toggle = (WidgetTypes.Toggle) overrideAnnotation;
                switch (toggle.value()) {
                    case BOX:
                        return (OptionWidget<E>) new ToggleBoxOptionWidget(boolOpt);
                    case ACTION_BUTTON:
                        return (OptionWidget<E>) new ToggleActionButtonOptionWidget(boolOpt);
                    default:
                        return (OptionWidget<E>) new ToggleCapsuleOptionWidget(boolOpt);
                }
            }
            return (OptionWidget<E>) new ToggleCapsuleOptionWidget(boolOpt);
        }

        if (type == int.class || type == Integer.class) {
            Option<Integer> intOpt = (Option<Integer>) option;
            if (overrideAnnotation instanceof WidgetTypes.ColorPicker) {
                return (OptionWidget<E>) new ColorPickerOptionWidget(intOpt);
            }
            if (overrideAnnotation instanceof WidgetTypes.SliderInt) {
                WidgetTypes.SliderInt s =  (WidgetTypes.SliderInt) overrideAnnotation;
                return (OptionWidget<E>) SliderOptionWidget.ofInt(intOpt, s.min(), s.max(), s.step());
            }
            return (OptionWidget<E>) SliderOptionWidget.ofInt(intOpt, 0, 500, 1);
        }

        if (type == double.class || type == Double.class) {
            Option<Double> doubleOpt = (Option<Double>) option;
            if (overrideAnnotation instanceof WidgetTypes.SliderDouble) {
                WidgetTypes.SliderDouble s =  (WidgetTypes.SliderDouble) overrideAnnotation;
                return (OptionWidget<E>) SliderOptionWidget.ofDouble(doubleOpt, s.min(), s.max(), s.step());
            }
            return (OptionWidget<E>) SliderOptionWidget.ofDouble(doubleOpt, 0.0d, 1.0d, 0.01d);
        }

        if (type == float.class || type == Float.class) {
            Option<Float> floatOpt = (Option<Float>) option;
            if (overrideAnnotation instanceof WidgetTypes.SliderFloat) {
                WidgetTypes.SliderFloat s =  (WidgetTypes.SliderFloat) overrideAnnotation;
                return (OptionWidget<E>) SliderOptionWidget.ofFloat(floatOpt, s.min(), s.max(), s.step());
            }
            return (OptionWidget<E>) SliderOptionWidget.ofFloat(floatOpt, 0.0f, 1.0f, 0.01f);
        }

        if (type == String.class) {
            return (OptionWidget<E>) new InputStringOptionWidget((Option<String>) option, s -> true, s -> true, null);
        }

        if (net.minecraft.world.item.Item.class.isAssignableFrom(type)) {
            return (OptionWidget<E>) new InputItemOptionWidget((Option<net.minecraft.world.item.Item>) option);
        }

        if (type.isEnum()) {
            return (OptionWidget<E>) CyclingOptionWidget.of(
                (Option<Enum>) option,
                (Class<Enum>) type,
                e -> ComponentCompat.literal(e.name())
            );
        }

        return null;
    }

    static {
        register((field, option) -> {
            Class<?> type = field.getType();
            if (type != boolean.class && type != Boolean.class) return null;

            Option<Boolean> boolOpt = (Option<Boolean>) (Object) option;

            if (field.isAnnotationPresent(WidgetTypes.Toggle.class)) {
                WidgetTypes.Toggle toggle = field.getAnnotation(WidgetTypes.Toggle.class);
                switch (toggle.value()) {
                    case BOX:
                        return new ToggleBoxOptionWidget(boolOpt);
                    case ACTION_BUTTON:
                        return new ToggleActionButtonOptionWidget(boolOpt);
                    default:
                        return new ToggleCapsuleOptionWidget(boolOpt);
                }
            }
            return new ToggleCapsuleOptionWidget(boolOpt);
        });

        // Integer
        register((field, option) -> {
            Class<?> type = field.getType();
            if (type != int.class && type != Integer.class) return null;

            if (field.isAnnotationPresent(WidgetTypes.ColorPicker.class)) {
                return new ColorPickerOptionWidget((Option<Integer>) (Object) option);
            }
            if (field.isAnnotationPresent(WidgetTypes.SliderInt.class)) {
                WidgetTypes.SliderInt s = field.getAnnotation(WidgetTypes.SliderInt.class);
                return SliderOptionWidget.ofInt((Option<Integer>) (Object) option, s.min(), s.max(), s.step());
            }
            return SliderOptionWidget.ofInt((Option<Integer>) (Object) option, 0, 500, 1);
        });

        // Double
        register((field, option) -> {
            Class<?> type = field.getType();
            if (type != double.class && type != Double.class) return null;

            if (field.isAnnotationPresent(WidgetTypes.SliderDouble.class)) {
                WidgetTypes.SliderDouble s = field.getAnnotation(WidgetTypes.SliderDouble.class);
                return SliderOptionWidget.ofDouble((Option<Double>) (Object) option, s.min(), s.max(), s.step());
            }
            return SliderOptionWidget.ofDouble((Option<Double>) (Object) option, 0.0d, 1.0d, 0.01d);
        });

        // Float
        register((field, option) -> {
            Class<?> type = field.getType();
            if (type != float.class && type != Float.class) return null;

            if (field.isAnnotationPresent(WidgetTypes.SliderFloat.class)) {
                WidgetTypes.SliderFloat s = field.getAnnotation(WidgetTypes.SliderFloat.class);
                return SliderOptionWidget.ofFloat((Option<Float>) (Object) option, s.min(), s.max(), s.step());
            }
            return SliderOptionWidget.ofFloat((Option<Float>) (Object) option, 0.0f, 1.0f, 0.01f);
        });

        // String
        register((field, option) -> {
            if (field.getType() != String.class) return null;
            return new InputStringOptionWidget((Option<String>) (Object) option, s -> true, s -> true, null);
        });

        // Item
        register((field, option) -> {
            Class<?> type = field.getType();
            if (net.minecraft.world.item.Item.class.isAssignableFrom(type) || field.isAnnotationPresent(WidgetTypes.InputItem.class)) {
                return new InputItemOptionWidget((Option<net.minecraft.world.item.Item>) (Object) option);
            }
            return null;
        });

        // Enum
        register((field, option) -> {
            if (field.isAnnotationPresent(WidgetTypes.Cycling.class) || field.getType().isEnum()) {
                Class<Enum> enumClass = (Class<Enum>) field.getType();
                return CyclingOptionWidget.of(
                    (Option<Enum>) (Object) option,
                    enumClass,
                    e -> ComponentCompat.literal(e.name())
                );
            }
            return null;
        });

        // Keybind
        register((field, option) -> {
            if (field.isAnnotationPresent(WidgetTypes.Keybind.class)) {
                return new KeybindOptionWidget((Option<List<InputConstants.Key>>) (Object) option);
            }
            return null;
        });

        // List
        register((field, option) -> {
            if (!List.class.isAssignableFrom(field.getType()) || !field.isAnnotationPresent(WidgetTypes.List.class)) {
                return null;
            }
            return AnnotationParser.createListWidgetForField(field, (Option<List<Object>>) (Object) option);
        });
    }
}
