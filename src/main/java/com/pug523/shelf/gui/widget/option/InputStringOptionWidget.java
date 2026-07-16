package com.pug523.shelf.gui.widget.option;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.pug523.shelf.compat.ComponentCompat;
import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;

import com.pug523.shelf.gui.widget.TextInputFieldWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class InputStringOptionWidget extends OptionWidget<String> {
    private final TextInputFieldWidget<String> textField;

    public InputStringOptionWidget(GuiOption<String> option, Predicate<String> filter, Predicate<String> validator, Consumer<String> responder) {
        // TODO: i18n
        this(option, filter, validator, responder, ComponentCompat.translatable("string..."));
    }
    public InputStringOptionWidget(GuiOption<String> option, Predicate<String> filter, Predicate<String> validator, Consumer<String> responder, Component hint) {
        super(option);

        this.textField = new TextInputFieldWidget<>(
            true,
            filter,
            validator,
            text -> {
                this.setPendingValue(text);
                if (responder != null) {
                    responder.accept(text);
                }
            },
            null,
            this.getPendingValue()
        );
        this.textField.setHint(hint);
        this.textField.setAlwaysUnderlined(true);
    }

    public void setMaxLength(int length) {
        this.textField.setMaxLength(length);
    }

    public void setText(String text) {
        this.textField.setText(text);
    }

    public String getText() {
        return this.textField.getText();
    }

    public boolean isFocused() {
        return this.textField.isFocused();
    }

    public void setFocused(boolean focus) {
        this.textField.setFocused(focus);
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX, int mouseY) {
        this.textField.setText(this.getPendingValue());
        LayoutConfig cfg = layout.getConfig();
        this.textField.setMaxLength(cfg.stringInputMaxLength);

        int leftTextPadding = cfg.stringInputLeftTextPadding;
        int targetX = x + leftTextPadding;
        int targetY = y + (height - font.lineHeight) / 2 + cfg.stringInputTextOffsetY;
        int targetWidth = Math.max(cfg.stringInputMinTextFieldWidth, width - leftTextPadding - layout.optionWidgetRightMargin);

        this.textField.render(font, gui, layout, targetX, targetY, targetWidth, font.lineHeight, mouseX, mouseY);
    }

    @Override
    public void resetPendingToDefault() {
        super.resetPendingToDefault();
        this.textField.setText(this.getPendingValue());
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
        setFocused(focus);
    }
}
