package com.pug523.shelf.gui.widget;

import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.config.Option;
import com.pug523.shelf.gui.input.InputUtil;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.renderer.RenderUtil;
import com.pug523.shelf.gui.sound.SoundUtil;

import net.minecraft.client.gui.Font;

public class BooleanOptionWidget extends OptionWidget<Boolean> {
    private LayoutConfig cachedConfig;
    private final boolean round;
    private int cachedX, cachedY, cachedWidth, cachedHeight;

    public BooleanOptionWidget(Option<Boolean> option, boolean round) {
        super(option);
        this.round = round;
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX,
            int mouseY) {
        this.cachedX = x;
        this.cachedY = y;
        this.cachedWidth = width;
        this.cachedHeight = height;
        this.cachedConfig = layout.getConfig();

        LayoutConfig cfg = layout.getConfig();

        int sx = switchX(cfg);
        int sy = switchY(cfg);

        boolean val = option.getPendingValue().booleanValue();
        boolean isHovered = isGenerouslyHovered(mouseX, mouseY, cfg);

        int bgBoxColor;
        if (val) {
            bgBoxColor = isHovered ? cfg.colorToggleBgOnHover : cfg.colorToggleBgOn;
        } else {
            bgBoxColor = isHovered ? cfg.colorToggleBgOffHover : cfg.colorToggleBgOff;
        }

        if (round) {
            RenderUtil.drawDynamicCapsule(gui, sx, sy, cfg.toggleSwitchWidth, cfg.toggleSwitchHeight, bgBoxColor);
        } else {
            gui.fill(sx, sy, sx + cfg.toggleSwitchWidth, sy + cfg.toggleSwitchHeight, bgBoxColor);
        }

        int knobHeight = cfg.toggleSwitchHeight - 6;
        int knobWidth = knobHeight;
        int knobY = sy + (cfg.toggleSwitchHeight - knobHeight) / 2;
        int paddingX = (cfg.toggleSwitchHeight - knobHeight) / 2;
        int knobX = val ? (sx + cfg.toggleSwitchWidth - knobWidth - paddingX) : (sx + paddingX);

        if (round) {
            int radius = knobWidth / 2;
            int centerX = knobX + radius;
            int centerY = knobY + radius;
            RenderUtil.drawDynamicCircle(gui, centerX, centerY, radius, cfg.colorToggleKnob);
        } else {
            gui.fill(knobX, knobY, knobX + knobWidth, knobY + knobHeight, cfg.colorToggleKnob);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == InputUtil.LEFT_MOUSE_BUTTON && cachedConfig != null) {
            if (isGenerouslyHovered(mouseX, mouseY, cachedConfig)) {
                toggle();
                SoundUtil.clickSound();
                return true;
            }
        }
        return false;
    }

    private void toggle() {
        option.setPendingValue(!option.getPendingValue());
    }

    private boolean isGenerouslyHovered(double mouseX, double mouseY, LayoutConfig cfg) {
        int sx = switchX(cfg);
        int sy = switchY(cfg);
        return mouseX >= sx - cfg.toggleHitboxPadding && mouseX <= sx + cfg.toggleSwitchWidth + cfg.toggleHitboxPadding
                && mouseY >= sy - cfg.toggleHitboxPadding
                && mouseY <= sy + cfg.toggleSwitchHeight + cfg.toggleHitboxPadding;
    }

    private int switchX(LayoutConfig cfg) {
        return cachedX + cachedWidth - cfg.toggleSwitchWidth - cfg.togglePaddingRight;
    }

    private int switchY(LayoutConfig cfg) {
        return cachedY + (cachedHeight - cfg.toggleSwitchHeight) / 2;
    }
}
