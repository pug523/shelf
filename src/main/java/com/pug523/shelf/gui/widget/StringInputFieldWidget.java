package com.pug523.shelf.gui.widget;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.pug523.shelf.compat.ComponentCompat;
import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.config.Option;
import com.pug523.shelf.gui.layout.LayoutEngine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
//#if MC >= 12109
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
//#endif
import net.minecraft.network.chat.Component;

public class StringInputFieldWidget extends OptionWidget<String> {
    protected EditBox editBox;
    private final Predicate<String> validator;
    private final Consumer<String> responder;

    public StringInputFieldWidget(Option<String> option, Predicate<String> validator, Consumer<String> responder) {
        super(option);
        this.validator = validator;
        this.responder = responder;

        // TODO: move magic numbers to layout config
        rebuildEditBox(Minecraft.getInstance().font, 0, 0, 100, 20, ComponentCompat.empty());
    }

    private void rebuildEditBox(Font font, int x, int y, int width, int height, Component narration) {
        //#if MC >= 11600
        this.editBox = new EditBox(font, x, y, width, height, narration);
        //#else
        //$$ this.editBox = new EditBox(font, x, y, width, height, narration.getString());
        //#endif
        this.editBox.setValue(option.getPendingValue());
        this.editBox.setResponder(text -> {
            if (this.validator.test(text)) {
                this.editBox.setTextColor(0xFFFFFF);
                this.option.setPendingValue(text);
                if (this.responder != null) {
                    this.responder.accept(text);
                }
            } else {
                this.editBox.setTextColor(0xFF5555);
            }
        });
    }

    private boolean isHovered(double mouseX, double mouseY) {
        //#if MC >= 11900
        return mouseX >= this.editBox.getX() && mouseX < this.editBox.getX() + this.editBox.getWidth() && mouseY >= this.editBox.getY() && mouseY < this.editBox.getY() + this.editBox.getHeight();
        //#elseif MC >= 11600
        //$$ return mouseX >= this.editBox.x && mouseX < this.editBox.x + this.editBox.getWidth()
        //$$         && mouseY >= this.editBox.y && mouseY < this.editBox.y + this.editBox.getHeight();
        //#else
        //$$ return mouseX >= this.editBox.x && mouseX < this.editBox.x + this.editBox.width
        //$$         && mouseY >= this.editBox.y && mouseY < this.editBox.y + this.editBox.height;
        //#endif
    }

    private void setFocused(boolean focus) {
        //#if MC >= 11900
        this.editBox.setFocused(focus);
        //#else
        //$$ this.editBox.setFocus(focus);
        //#endif
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX, int mouseY) {
        String value = this.option.getPendingValue();
        if (!value.equals(this.editBox.getValue())) {
            this.editBox.setValue(value);
        }

        // TODO: move magic numbers to layout config
        int leftTextPadding = 150;

        int targetX = x + leftTextPadding;
        int targetY = y + (height - 20) / 2;
        int targetWidth = Math.max(50, width - leftTextPadding - layout.optionWidgetRightMargin);
        int targetHeight = 20;

        //#if MC >= 11900
        if (this.editBox.getX() != targetX) {
            this.editBox.setX(targetX);
        }
        if (this.editBox.getY() != targetY) {
            this.editBox.setY(targetY);
        }
        //#else
        //$$ if (this.editBox.x != targetX) {
        //$$     this.editBox.x = targetX;
        //$$ }
        //$$ if (this.editBox.y != targetY) {
        //$$     this.editBox.y = targetY;
        //$$ }
        //#endif

        //#if MC >= 11600
        if (this.editBox.getWidth() != targetWidth) {
            this.editBox.setWidth(targetWidth);
        }
        if (this.editBox.getHeight() != targetHeight) {
            // @formatter:off
            //#if MC >= 12002
            this.editBox.setHeight(targetHeight);
            //#else
            //$$ rebuildEditBox(font, targetX, targetY, targetWidth, targetHeight, ComponentCompat.empty());
            //#endif
            // @formatter:on
        }
        //#else
        //$$ if (this.editBox.width != targetWidth || this.editBox.height != targetHeight) {
        //$$     rebuildEditBox(font, targetX, targetY, targetWidth, targetHeight, ComponentCompat.empty());
        //$$ }
        //#endif

        this.editBox.setTextColor(0xFFFFFFFF);

        //#if MC >= 12000
        this.editBox.extractWidgetRenderState(gui.getGraphics(), mouseX, mouseY, 0.0f);
        //#elseif MC >= 11904
        //$$ this.editBox.renderWidget(gui.getPoseStack(), mouseX, mouseY, 0.0f);
        //#elseif MC >= 11600
        //$$ this.editBox.renderButton(gui.getPoseStack(), mouseX, mouseY, 0.0f);
        //#else
        //$$ this.editBox.renderButton(mouseX, mouseY, 0.0f);
        //#endif
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        setFocused(isHovered(mouseX, mouseY));

        //#if MC >= 12109
        return this.editBox.mouseClicked(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, modifiers)), false);
        //#else
        //$$ return this.editBox.mouseClicked(mouseX, mouseY, button);
        //#endif
    }

    @Override
    public boolean keyPressed(int keycode, int scancode, int modifiers, LayoutEngine layout) {
        //#if MC >= 12109
        return this.editBox.keyPressed(new KeyEvent(keycode, scancode, modifiers));
        //#else
        //$$ return this.editBox.keyPressed(keycode, scancode, modifiers);
        //#endif
    }

    @Override
    public boolean charTyped(int codepoint, int modifiers, LayoutEngine layout) {
        //#if MC >= 260000
        return this.editBox.charTyped(new CharacterEvent(codepoint));
        //#elseif MC >= 12109
        //$$ return this.editBox.charTyped(new CharacterEvent(codepoint, modifiers));
        //#else
        //$$ return this.editBox.charTyped((char) codepoint, modifiers);
        //#endif
    }

    @Override
    public void focusChanged(boolean focus, LayoutEngine layout) {
        if (!focus) {
            setFocused(false);
        }
    }
}
