package com.pug523.shelf.gui.widget;

import com.pug523.shelf.compat.ComponentCompat;
import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.compat.ScreenCompat;
import com.pug523.shelf.gui.ConfigScreen;
import com.pug523.shelf.gui.controller.SearchBarController;
import com.pug523.shelf.gui.layout.LayoutEngine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;

public class SearchBarWidget implements ClickableWidget {
    private final SearchBarController controller;
    private final TextInputFieldWidget<String> textField;

    public SearchBarWidget(SearchBarController controller) {
        this.controller = controller;
        this.textField = new TextInputFieldWidget<>(
            false,
            s -> true,
            s -> true,
            this.controller::setQuery,
            null,
            ""
        );
        this.textField.setAlwaysUnderlined(true);
        // TODO: i18n
        this.textField.setHint(ComponentCompat.translatable("search..."));

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
