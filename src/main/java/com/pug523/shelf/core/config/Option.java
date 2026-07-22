package com.pug523.shelf.core.config;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.pug523.shelf.common.compat.ComponentCompat;

import com.pug523.shelf.ui.option.GuiOption;
import net.minecraft.network.chat.Component;

public class Option<T> implements GuiOption<T> {
    private final Component name;
    private final String descriptionKey;
    private final T defaultValue;
    private final Supplier<T> getter;
    private final Consumer<T> setter;
    private final List<Tag> tags;

    private T pendingValue;

    public Option(Component name, String descriptionKey, T defaultValue, Supplier<T> getter, Consumer<T> setter,
            List<Tag> tags) {
        this.name = name;
        this.descriptionKey = descriptionKey;
        this.defaultValue = defaultValue;
        this.getter = getter;
        this.setter = setter;
        this.tags = tags;
        this.pendingValue = getter.get();
    }

    public Option(String nameKey, String descriptionKey, T defaultValue, Supplier<T> getter, Consumer<T> setter,
            List<Tag> tags) {
        this(ComponentCompat.translatable(nameKey), descriptionKey, defaultValue, getter, setter, tags);
    }

    public Option(String nameKey, T defaultValue, Supplier<T> getter, Consumer<T> setter, List<Tag> tags) {
        this(nameKey, nameKey + "_desc", defaultValue, getter, setter, tags);
    }

    public Component getName() {
        return name;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public Component getDescription() {
        return ComponentCompat.translatable(this.descriptionKey);
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public T getPendingValue() {
        return pendingValue;
    }

    public void setPendingValue(T value) {
        this.pendingValue = value;
    }

    public T getActualValue() {
        return getter.get();
    }

    public List<Tag> tags() {
        return tags;
    }

    public boolean isPendingModifiedFromDefault() {
        return !Objects.equals(this.pendingValue, this.defaultValue);
    }

    public boolean isPendingModifiedFromActual() {
        return !Objects.equals(this.pendingValue, getActualValue());
    }

    public void applyPendingToActual() {
        setter.accept(this.pendingValue);
    }

    public void discardPending() {
        this.pendingValue = getActualValue();
    }

    public void resetPendingToDefault() {
        this.pendingValue = defaultValue;
    }
}
