package com.pug523.shelf.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import com.pug523.shelf.compat.ComponentCompat;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.renderer.RenderUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.sound.SoundUtil;

public class ActionButtonWidget implements ClickableWidget {
    private Component label;
    private final Consumer<ActionButtonWidget> onPress;
    private boolean enabled = true;
    private boolean visible = true;
    private boolean hovered = false;
    private boolean silent = false;

    private long hoverStartTime = -1L;

    public ActionButtonWidget(Component label, Consumer<ActionButtonWidget> onPress) {
        this.label = label;
        this.onPress = onPress;
    }

    public void renderWithBackground(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX, int mouseY, int background) {
        gui.fill(x, y, x + width, y + height, background);
        render(font, gui, layout, x, y, width, height, mouseX, mouseY);
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX, int mouseY) {
        if (!this.visible) {
            return;
        }
        LayoutConfig cfg = layout.getConfig();

        this.hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        int backgroundColor;
        int textColor;
        if (!this.enabled) {
            backgroundColor = cfg.colorButtonBackgroundDisabled;
            textColor = cfg.colorButtonTextDisabled;
        } else if (this.hovered) {
            backgroundColor = cfg.colorButtonBackgroundHover;
            textColor = cfg.colorButtonText;
        } else {
            backgroundColor = cfg.colorButtonBackground;
            textColor = cfg.colorButtonText;
        }

        int borderThickness = cfg.actionButtonBorderThickness;
        RenderUtil.renderOutline(gui, x, y, width, height, borderThickness, cfg.colorButtonBorder);
        RenderUtil.renderInner(gui, x, y, width, height, borderThickness, backgroundColor);

        int textWidth = ComponentCompat.width(font, this.label);

        int padding = cfg.actionButtonPadding;
        int maxTextWidth = width - (borderThickness * 2) - (padding * 2);

        int textY = y + (height - font.lineHeight) / 2 + cfg.actionButtonTextOffsetY;

        if (textWidth > maxTextWidth) {
            if (this.hovered) {
                if (this.hoverStartTime == -1L) {
                    this.hoverStartTime = System.currentTimeMillis();
                }
            } else {
                this.hoverStartTime = -1L;
            }

            long duration = this.hoverStartTime != -1L ? System.currentTimeMillis() - this.hoverStartTime : 0L;

            int overflow = textWidth - maxTextWidth;

            long startWait = cfg.actionButtonScrollStartWait;
            long scrollTime = overflow * cfg.actionButtonScrollSpeed;
            long endWait = cfg.actionButtonScrollEndWait;
            long totalCycleTime = startWait + scrollTime + endWait;

            long cycleTime = duration % totalCycleTime;

            double progress = 0.0;
            if (cycleTime > startWait && cycleTime <= startWait + scrollTime) {
                progress = (double) (cycleTime - startWait) / (double) scrollTime;
            } else if (cycleTime > startWait + scrollTime) {
                progress = 1.0;
            }

            int offset = (int) (progress * overflow);

            int textX = x + borderThickness + padding;
            gui.enableScissor(x + borderThickness, y + borderThickness, x + borderThickness + width - (borderThickness * 2), y + borderThickness + height - (borderThickness * 2));
            gui.text(font, this.label, textX - offset, textY, textColor, cfg.actionButtonShadow);
            gui.disableScissor();
        } else {
            this.hoverStartTime = -1L;
            int textX = x + (width - textWidth) / 2;
            gui.text(font, this.label, textX, textY, textColor, cfg.actionButtonShadow);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        if (!this.visible || !this.enabled) {
            return false;
        }

        if (button == InputConstants.MOUSE_BUTTON_LEFT && this.hovered) {
            this.onPress();
            return true;
        }
        return false;
    }

    public void onPress() {
        if (this.enabled && this.visible) {
            this.onPress.accept(this);
            if (!silent) {
                SoundUtil.clickSound();
            }
        }
    }

    public Component getLabel() {
        return label;
    }

    public void setLabel(Component label) {
        this.label = label;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }
}
