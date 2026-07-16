package com.pug523.shelf.api.annotation;

import java.lang.annotation.*;
import java.util.function.Supplier;

public class WidgetTypes {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Toggle {
        enum Style {CAPSULE, BOX, ACTION_BUTTON}

        Style value() default Style.CAPSULE;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface SliderInt {
        int min();

        int max();

        int step() default 1;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface SliderDouble {
        double min();

        double max();

        double step() default 0.01d;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface SliderFloat {
        float min();

        float max();

        float step() default 0.01f;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ColorPicker {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface InputString {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface InputItem {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Cycling {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Keybind {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface List {
        Class<? extends Annotation> value() default Annotation.class;

        Class<? extends Supplier<?>> creator() default DefaultCreator.class;

        interface DefaultCreator extends Supplier<Object> {
            @Override
            default Object get() {
                return null;
            }
        }
    }
}

