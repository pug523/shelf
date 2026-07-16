package com.pug523.shelf.gui.widget.option;

import com.pug523.shelf.config.Option;
import com.pug523.shelf.gui.widget.ClickableWidget;

public abstract class OptionWidget<T> implements ClickableWidget {
    // TODO: remove the option delegate and make it loosely coupled.

    protected final Option<T> option;

    public OptionWidget(Option<T> option) {
        this.option = option;
    }

    public Option<T> getOption() {
        return option;
    }

    public T getDefaultValue() {
        return option.getDefaultValue();
    }

    public T getPendingValue() {
        return option.getPendingValue();
    }

    public void setPendingValue(T value) {
        option.setPendingValue(value);
    }

    public T getActualValue() {
        return option.getActualValue();
    }

    public boolean isPendingModifiedFromDefault() {
        return option.isPendingModifiedFromDefault();
    }

    public boolean isPendingModifiedFromActual() {
        return option.isPendingModifiedFromActual();
    }

    public void applyPendingToActual() {
        option.applyPendingToActual();
    }

    public void discardPending() {
        option.discardPending();
    }

    public void resetPendingToDefault() {
        option.resetPendingToDefault();
    }
}
