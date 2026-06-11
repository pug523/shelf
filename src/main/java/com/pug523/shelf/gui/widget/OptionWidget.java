package com.pug523.shelf.gui.widget;

import com.pug523.shelf.config.Option;

public abstract class OptionWidget<T> implements ClickableWidget {
    protected final Option<T> option;

    public OptionWidget(Option<T> option) {
        this.option = option;
    }

    public Option<T> getOption() {
        return option;
    }
}
