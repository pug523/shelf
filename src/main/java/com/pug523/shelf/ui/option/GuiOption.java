package com.pug523.shelf.ui.option;

import net.minecraft.network.chat.Component;

// TODO: refactor (should be removed, create OptionModel and OptionViewModel)
// model should not know about minecraft component
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
