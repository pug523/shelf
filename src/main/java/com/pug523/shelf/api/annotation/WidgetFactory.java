package com.pug523.shelf.api.annotation;

import com.pug523.shelf.config.Option;
import com.pug523.shelf.gui.widget.option.OptionWidget;
import java.lang.reflect.Field;

@FunctionalInterface
public interface WidgetFactory {
    OptionWidget<?> create(Field field, Option<Object> option);
}
