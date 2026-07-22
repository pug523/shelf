package com.pug523.shelf.api.annotation;

import com.pug523.shelf.core.config.Option;
import com.pug523.shelf.ui.option.OptionWidget;
import java.lang.reflect.Field;

@FunctionalInterface
public interface WidgetFactory {
    OptionWidget<?> create(Field field, Option<Object> option);
}
