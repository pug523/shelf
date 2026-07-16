package com.pug523.shelf.gui.widget.option;

import com.pug523.shelf.gui.widget.ClickableWidget;
import net.minecraft.network.chat.Component;

public abstract class OptionWidget<T> implements ClickableWidget {
    protected final GuiOption<T> option;

    public OptionWidget(GuiOption<T> boundValue) {
        this.option = boundValue;
    }

    public GuiOption<T> getOption() { return option; }
    public Component getName() { return option.getName(); }
    public String getDescriptionKey() { return option.getDescriptionKey(); }
    public Component getDescription() { return option.getDescription(); }
    public T getDefaultValue() { return option.getDefaultValue(); }
    public T getPendingValue() { return option.getPendingValue(); }
    public void setPendingValue(T value) { option.setPendingValue(value); }
    public T getActualValue() { return option.getActualValue(); }
    public boolean isPendingModifiedFromDefault() { return option.isPendingModifiedFromDefault(); }
    public boolean isPendingModifiedFromActual() { return option.isPendingModifiedFromActual(); }
    public void applyPendingToActual() { option.applyPendingToActual(); }
    public void discardPending() { option.discardPending(); }
    public void resetPendingToDefault() { option.resetPendingToDefault(); }
}
