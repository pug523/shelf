package com.pug523.shelf.api.annotation;

import java.lang.annotation.*;
import java.util.function.Supplier;

/// Defines the UI widget mappings available for configuration fields.
/// Attach these annotations to fields inside your configuration classes to customize
/// how options are rendered and manipulated within the configuration screens.
public class WidgetTypes {

    /// Renders a boolean option as a toggleable switch or button.
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Toggle {
        enum Style {CAPSULE, BOX, ACTION_BUTTON}

        /// Configures the visual appearance of the toggle widget.
        Style value() default Style.CAPSULE;
    }

    /// Renders an integer value using an adjustable horizontal slider bar.
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface SliderInt {
        /// The minimum selectable value.
        int min();

        /// The maximum selectable value.
        int max();

        /// The interval increment when dragging the slider.
        int step() default 1;
    }

    /// Renders a double precision floating-point value using an adjustable slider bar.
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface SliderDouble {
        /// The minimum selectable value.
        double min();

        /// The maximum selectable value.
        double max();

        /// The interval increment when dragging the slider.
        double step() default 0.01d;
    }

    /// Renders a single precision floating-point value using an adjustable slider bar.
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface SliderFloat {
        /// The minimum selectable value.
        float min();

        /// The maximum selectable value.
        float max();

        /// The interval increment when dragging the slider.
        float step() default 0.01f;
    }

    /// Renders an RGBA/ARGB color selection tool overlay for an integer option field.
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ColorPicker {
    }

    /// Renders a standard editable text input field for a string option.
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface InputString {
    }

    /// Renders an item search and selection box tailored specifically for Minecraft Item profiles.
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface InputItem {
    }

    /// Renders an option that cycles sequentially through available values upon being clicked.
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Cycling {
    }

    /// Renders an interactive keybinding detection element for capture and input assignments.
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Keybind {
    }

    /// Renders a complex collections list widget capable of adding, removing, and altering sub-elements.
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface List {
        /// Specifies the inner configuration widget annotation applied to elements inside this collection list.
        Class<? extends Annotation> value() default Annotation.class;

        Class<? extends Supplier<?>> creator() default DefaultCreator.class;

        interface DefaultCreator extends Supplier<Object> {
            @Override
            default Object get() {
                return null;
            }
        }
    }

    /// Renders a dropdown menu allowing selection of **exactly one** item from a pool of dynamic candidates or enums.
    ///
    /// ### Examples:
    /// ```java
    /// // Automatic Enum Binding:
    /// @WidgetTypes.Selector
    /// public MyEnum activeMode = MyEnum.DEFAULT;
    ///
    /// // Dynamic Lists via Candidate Providers:
    /// @WidgetTypes.Selector(candidates = MyBlockListSupplier.class)
    /// public String targetBlockId = "minecraft:stone";
    /// ```
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Selector {
        /// Explicitly links selection possibilities to an alternative Enum archetype layout structure.
        Class<? extends Enum<?>> enumClass() default DefaultEnum.class;

        /// Provides a runtime data generator class used to populate the single selection menu container items.
        Class<? extends Supplier<? extends java.util.List<?>>> candidates() default DefaultCandidates.class;

        enum DefaultEnum {}

        interface DefaultCandidates extends Supplier<java.util.List<Object>> {
            @Override
            default java.util.List<Object> get() {
                return java.util.Collections.emptyList();
            }
        }
    }

    /// Renders a multi-choice drop-down panel list allowing users to check or uncheck multiple active elements simultaneously.
    /// This annotation must be placed on an option field assigning or managing a `java.util.List` data state.
    ///
    /// ### Example:
    /// ```java
    /// @WidgetTypes.MultiSelector(candidates = CustomItemSupplier.class)
    /// public List<String> excludedItems = new ArrayList<>();
    /// ```
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface MultiSelector {
        /// Provides a runtime data generator class used to populate the multi-selection menu checkboxes.
        Class<? extends Supplier<? extends java.util.List<?>>> candidates() default DefaultCandidates.class;

        interface DefaultCandidates extends Supplier<java.util.List<Object>> {
            @Override
            default java.util.List<Object> get() {
                return java.util.Collections.emptyList();
            }
        }
    }
}

