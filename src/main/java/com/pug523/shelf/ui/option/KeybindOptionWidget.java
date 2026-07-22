package com.pug523.shelf.ui.option;

import com.mojang.blaze3d.platform.InputConstants;
import com.pug523.shelf.common.compat.ComponentCompat;
import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.ui.layout.LayoutConfig;
import com.pug523.shelf.ui.layout.LayoutEngine;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

// TODO: refactor
public class KeybindOptionWidget extends OptionWidget<List<InputConstants.Key>> {
    private static final Component COMPONENT_UNBOUND = ComponentCompat.literal("Unbound").withStyle(ChatFormatting.GRAY);
    private static final Component COMPONENT_CONCAT = ComponentCompat.literal(" + ").withStyle(ChatFormatting.GRAY);
    private static final Component COMPONENT_RECORDING_PREFIX = ComponentCompat.literal("> ").withStyle(ChatFormatting.YELLOW);
    private static final Component COMPONENT_RECORDING_SUFFIX = ComponentCompat.literal(" <").withStyle(ChatFormatting.YELLOW);

    private final ActionButtonWidget buttonDelegate;
    private boolean recording = false;

    public KeybindOptionWidget(GuiOption<List<InputConstants.Key>> option) {
        super(option);
        this.buttonDelegate = new ActionButtonWidget(COMPONENT_UNBOUND, btn -> this.toggleRecordingStatus());
    }

    private void toggleRecordingStatus() {
        if (recording) {
            endRecording();
        } else {
            startRecording();
        }
    }

    private void updateLabel() {
        if (!recording && isUnbound()) {
            buttonDelegate.setLabel(COMPONENT_UNBOUND);
        } else {
            MutableComponent label = ComponentCompat.empty();
            if (recording) {
                label.append(COMPONENT_RECORDING_PREFIX);
            }
            boolean first = true;
            for (InputConstants.Key key : getPendingValue()) {
                if (!first) {
                    label.append(COMPONENT_CONCAT);
                } else {
                    first = false;
                }
                label.append(key.getDisplayName());
            }
            if (recording) {
                label.append(COMPONENT_RECORDING_SUFFIX);
            }
            buttonDelegate.setLabel(label);
        }
    }

    private boolean isUnbound() {
        return getPendingValue().isEmpty();
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX, int mouseY) {
        LayoutConfig cfg = layout.getConfig();

        updateLabel();
        int buttonWidthWithPadding = Math.min(cfg.keybindButtonMaxWidth, ComponentCompat.width(font, buttonDelegate.getLabel()) + cfg.keybindButtonWidthPadding);

        int btnX = x + width - layout.optionWidgetRightMargin - buttonWidthWithPadding;
        int btnY = y + (height - cfg.keybindButtonHeight) / 2;
        this.buttonDelegate.render(font, gui, layout, btnX, btnY, buttonWidthWithPadding, cfg.keybindButtonHeight, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        boolean result = this.buttonDelegate.mouseClicked(mouseX, mouseY, button, modifiers, layout);
        if (recording && !result) {
            InputConstants.Key mouseKey = InputConstants.Type.MOUSE.getOrCreate(button);
            addOneKey(mouseKey);
        }
        return result;
    }

    private void startRecording() {
        if (!recording) {
            recording = true;
            clearBind();
        }
    }

    private void endRecording() {
        if (recording) {
            recording = false;
            setPendingValue(getPendingValue());
        }
    }

    @Override
    public boolean keyPressed(int keycode, int scancode, int modifiers, LayoutEngine layout) {
        if (!recording) {
            return false;
        } else if (keycode == InputConstants.KEY_ESCAPE) {
            clearBind();
            endRecording();
            return true;
        }
        addOneKey(getKey(keycode, scancode));
        return true;
    }

    private void addOneKey(InputConstants.Key key) {
        List<InputConstants.Key> keys = getPendingValue();
        if (!keys.contains(key)) {
            keys.add(key);
            setPendingValue(keys);
        }
    }

    private void clearBind() {
        setPendingValue(new ArrayList<>());
    }

    private static InputConstants.Key getKey(int keycode, int scancode) {
        return keycode == -1 ? InputConstants.Type.SCANCODE.getOrCreate(scancode) : InputConstants.Type.KEYSYM.getOrCreate(keycode);
    }

    @Override
    public void focusChanged(boolean focus, LayoutEngine layout) {
        if (recording && !focus) {
            endRecording();
        }
    }
}
