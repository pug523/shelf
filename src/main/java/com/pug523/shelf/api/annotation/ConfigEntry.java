package com.pug523.shelf.api.annotation;

import java.lang.annotation.*;

/// Marks a field inside a configuration file layout as an editable configuration property entry.
/// This acts as the bridge connecting your physical storage fields directly to the user interface menu nodes.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigEntry {

    /// The unique localized string snippet (or absolute path if category is missing)
    /// used to resolve translation strings.
    String key();

    /// The local key modifier matching group elements hierarchy. Relies on relative paths if category is present.
    String group() default "";

    /// Assigns this configuration property element to a specific top-level category navigation tab.
    /// If empty, the root category `All Settings` is assumed and absolute lookups are used instead.
    String category() default "";

    /// Explicit translation identifier overriding the automatic fallback behavior (`key() + "_desc"`).
    String description() default "";
}
