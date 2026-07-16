package com.pug523.shelf.gui.widget.option;

import com.mojang.blaze3d.platform.InputConstants;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.sound.SoundUtil;

public abstract class ToggleOptionWidget extends OptionWidget<Boolean> {
    public ToggleOptionWidget(GuiOption<Boolean> option) {
        super(option);
    }

    protected boolean focus = false;

    protected void toggle() {
        setPendingValue(!getPendingValue());
        SoundUtil.clickSound();
    }

    @Override
    public boolean keyPressed(int keycode, int scancode, int modifiers, LayoutEngine layout) {
        if (keycode == InputConstants.KEY_RETURN && focus) {
            this.toggle();
            return true;
        }
        return false;
    }

    @Override
    public void focusChanged(boolean focus, LayoutEngine layout) {
        this.focus = focus;
    }
}
