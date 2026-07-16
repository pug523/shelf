package com.pug523.shelf.gui.widget.option;

import net.minecraft.network.chat.Component;

public interface GuiOption<T> {
    Component getName();

    String getDescriptionKey();

    Component getDescription();

    T getDefaultValue();

    T getPendingValue();

    void setPendingValue(T value);

    T getActualValue();

    boolean isPendingModifiedFromDefault();

    boolean isPendingModifiedFromActual();

    void applyPendingToActual();

    void discardPending();

    void resetPendingToDefault();
}
