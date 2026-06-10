package com.pug523.shelf.gui;

import com.pug523.shelf.config.Option;

import net.minecraft.network.chat.Component;

public record RenderableItem(Component text, Option<?> option, boolean isHeader) {
    public static RenderableItem createHeader(Component text) {
        return new RenderableItem(text, null, true);
    }

    public static RenderableItem createOption(Option<?> option) {
        return new RenderableItem(option.getName(), option, false);
    }
}
