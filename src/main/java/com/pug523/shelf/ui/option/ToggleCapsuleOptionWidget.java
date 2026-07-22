package com.pug523.shelf.ui.option;

import com.mojang.blaze3d.platform.InputConstants;
import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.ui.layout.LayoutConfig;
import com.pug523.shelf.ui.layout.LayoutEngine;
import com.pug523.shelf.ui.render.RenderUtil;

import net.minecraft.client.gui.Font;

// TODO: refactor
public class ToggleCapsuleOptionWidget extends ToggleOptionWidget {
    private float switchX = -1.0f;
    private float switchY = -1.0f;

    public ToggleCapsuleOptionWidget(GuiOption<Boolean> option) {
        super(option);
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX, int mouseY) {
        LayoutConfig cfg = layout.getConfig();

        switchX = calcSwitchX(layout, x, width);
        switchY = calcSwitchY(layout, y, height);

        boolean pendingValue = getPendingValue();
        boolean isHovered = isHovered(mouseX, mouseY, layout);

        int bgBoxColor;
        if (pendingValue) {
            bgBoxColor = isHovered ? cfg.colorToggleBgOnHover : cfg.colorToggleBgOn;
        } else {
            bgBoxColor = isHovered ? cfg.colorToggleBgOffHover : cfg.colorToggleBgOff;
        }

        if (cfg.roundedCapsule) {
            RenderUtil.renderCapsule(gui, switchX, switchY, cfg.capsuleToggleWidth, cfg.capsuleToggleHeight, bgBoxColor);
        } else {
            gui.fill((int) switchX, (int) switchY, (int) (switchX + cfg.capsuleToggleWidth), (int) (switchY + cfg.capsuleToggleHeight), bgBoxColor);
        }

        float knobSize = cfg.capsuleToggleHeight * (float) cfg.capsuleToggleKnobSizeFactor;
        float knobY = switchY + (cfg.capsuleToggleHeight - knobSize) / 2.0f;
        float paddingX = (cfg.capsuleToggleHeight - knobSize) / 2.0f;
        float knobX = pendingValue ? (switchX + cfg.capsuleToggleWidth - knobSize - paddingX) : (switchX + paddingX);

        if (cfg.roundedCapsule) {
            float radius = knobSize / 2.0f;
            float centerX = knobX + radius;
            float centerY = knobY + radius;
            RenderUtil.renderCircle(gui, centerX, centerY, radius, cfg.colorToggleKnob);
        } else {
            int maxX = (int) (knobX + knobSize);
            int maxY = (int) (knobY + knobSize);
            gui.fill((int) knobX, (int) knobY, maxX, maxY, cfg.colorToggleKnob);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        if (button == InputConstants.MOUSE_BUTTON_LEFT) {
            if (isHovered(mouseX, mouseY, layout)) {
                toggle();
                return true;
            }
        }
        return false;
    }

    private boolean isHovered(double mouseX, double mouseY, LayoutEngine layout) {
        float sx = switchX;
        float sy = switchY;
        if (sx < 0.0f || sy < 0.0f) {
            return false;
        }
        LayoutConfig cfg = layout.getConfig();
        return mouseX >= sx && mouseX <= sx + cfg.capsuleToggleWidth && mouseY >= sy && mouseY <= sy + cfg.capsuleToggleHeight;
    }

    private static float calcSwitchX(LayoutEngine layout, float x, float width) {
        return x + width - layout.getConfig().capsuleToggleWidth - layout.optionWidgetRightMargin;
    }

    private static float calcSwitchY(LayoutEngine layout, float y, float height) {
        return y + (height - layout.getConfig().capsuleToggleHeight) / 2.0f;
    }
}
