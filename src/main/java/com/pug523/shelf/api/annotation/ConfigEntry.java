package com.pug523.shelf.api.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigEntry {
    /// Translation key or display name
    String key();

    ///  Tab category translation key
    String category() default "";

    ///  Section group translation key
    String group() default "";
}
