package com.pug523.shelf.ui.component;

import com.pug523.shelf.common.compat.ComponentCompat;
import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.ui.screen.controller.SearchBarController;
import com.pug523.shelf.ui.layout.LayoutEngine;

import com.pug523.shelf.ui.widget.Widget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

// TODO: refactor after text input field fixed
public class SearchBarWidget implements Widget {
    private static final Style SEARCH_STYLE = Style.EMPTY.applyFormats(ChatFormatting.GRAY, ChatFormatting.ITALIC);
    // TODO: i18n
    private static final Component SEARCH_COMPONENT = ComponentCompat.translatable("search...").withStyle(SEARCH_STYLE);

    private final TextInputFieldWidget textField;

    public SearchBarWidget() {
        this.controller = controller;
        this.textField = new TextInputFieldWidget<>(
            false,
            s -> true,
            null,
            this.controller::setQuery,
            null,
            ""
        );
        this.textField.setAlwaysUnderlined(true);
        this.textField.setHint(SEARCH_COMPONENT);

        this.setFocused(true);
    }

    public SearchBarController getController() {
        return controller;
    }

    public boolean isFocused() {
        return this.textField.isFocused();
    }

    public void setFocused(boolean focus) {
        this.textField.setFocused(focus);
    }


    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX, int mouseY) {
        this.textField.render(font, gui, layout, x, y, width, height, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        return this.textField.mouseClicked(mouseX, mouseY, button, modifiers, layout);
    }

    @Override
    public boolean keyPressed(int keycode, int scancode, int modifiers, LayoutEngine layout) {
        return this.textField.keyPressed(keycode, scancode, modifiers, layout);
    }

    @Override
    public boolean charTyped(int codepoint, int modifiers, LayoutEngine layout) {
        return this.textField.charTyped(codepoint, modifiers, layout);
    }

    @Override
    public void focusChanged(boolean focus, LayoutEngine layout) {
        this.textField.setFocused(focus);
    }
}
