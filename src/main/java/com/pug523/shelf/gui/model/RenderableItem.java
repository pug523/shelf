package com.pug523.shelf.gui.model;

import com.pug523.shelf.gui.widget.option.OptionWidget;

import net.minecraft.network.chat.Component;

public class RenderableItem {

    private final boolean header;
    private final Component text;
    private final OptionWidget<?> widget;

    private RenderableItem(boolean header, Component text, OptionWidget<?> widget) {
        this.header = header;
        this.text = text;
        this.widget = widget;
    }

    public static RenderableItem header(Component text) {
        return new RenderableItem(true, text, null);
    }

    public static RenderableItem option(OptionWidget<?> widget) {
        return new RenderableItem(false, widget.getOption().getName(), widget);
    }

    public boolean isHeader() {
        return header;
    }

    public Component text() {
        return text;
    }

    public OptionWidget<?> widget() {
        return widget;
    }
}
