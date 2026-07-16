package com.pug523.shelf.gui.widget.option;

import com.mojang.blaze3d.platform.InputConstants;
import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.renderer.RenderUtil;

import net.minecraft.client.gui.Font;

public class ToggleBoxOptionWidget extends ToggleOptionWidget {
    private int cachedX, cachedY, cachedWidth, cachedHeight;

    public ToggleBoxOptionWidget(GuiOption<Boolean> option) {
        super(option);
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX, int mouseY) {
        this.cachedX = x;
        this.cachedY = y;
        this.cachedWidth = width;
        this.cachedHeight = height;
        LayoutConfig cfg = layout.getConfig();

        float switchX = getSwitchX(layout);
        float switchY = getSwitchY(layout);

        boolean pendingValue = getPendingValue();
        boolean isHovered = isHovered(mouseX, mouseY, layout);

        int color = isHovered ? cfg.colorToggleBoxHover : cfg.colorToggleBox;
        RenderUtil.renderOutline(gui, (int) switchX, (int) switchY, cfg.boxToggleWidth, cfg.boxToggleHeight, cfg.boxToggleOutlineThickness, color);
        if (pendingValue) {
            RenderUtil.renderInner(gui, (int) switchX, (int) switchY, cfg.boxToggleWidth, cfg.boxToggleHeight, cfg.boxToggleOutlineThickness + cfg.boxToggleInnerPadding, color);
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
        LayoutConfig cfg = layout.getConfig();
        float sx = getSwitchX(layout);
        float sy = getSwitchY(layout);
        float sMaxX = sx + cfg.boxToggleWidth;
        float sMaxY = sy + cfg.boxToggleHeight;
        return sx <= mouseX && mouseX <= sMaxX && sy <= mouseY && mouseY <= sMaxY;
    }

    private float getSwitchX(LayoutEngine layout) {
        return cachedX + cachedWidth - layout.getConfig().boxToggleWidth - layout.optionWidgetRightMargin;
    }

    private float getSwitchY(LayoutEngine layout) {
        return cachedY + (cachedHeight - layout.getConfig().boxToggleHeight) / 2.0f;
    }
}
