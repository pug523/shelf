package com.pug523.shelf.api.annotation;

import java.lang.annotation.*;

/// Marks a field inside a configuration file layout as an editable configuration property entry.
/// This acts as the bridge connecting your physical storage fields directly to the user interface menu nodes.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigEntry {

    /// The unique localization translation string identifier key used to display the readable name
    /// and descriptions for this property layout entry (e.g., `mymod.config.option.debug_mode`).
    String key();

    /// Assigns this configuration property element to a specific top-level category navigation tab
    /// layout identifier grouping inside the UI window panel.
    String category() default "";

    /// Places this configuration entry node into a distinct visual sub-section or card list box grouping panel
    /// nested right underneath its assigned parent category dashboard tab.
    String group() default "";
}
