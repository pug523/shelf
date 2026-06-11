package com.pug523.shelf.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import java.util.function.Consumer;

import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.gui.input.InputUtil;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.sound.SoundUtil;

public class ActionButtonWidget implements ClickableWidget {
    private final Component label;
    private final Consumer<ActionButtonWidget> onPress;
    private boolean enabled = true;
    private boolean visible = true;
    private boolean hovered = false;
    private boolean silent = false;

    // TODO: move this to layout config
    private boolean withShadow = true;

    public ActionButtonWidget(Component label, Consumer<ActionButtonWidget> onPress) {
        this.label = label;
        this.onPress = onPress;
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX,
            int mouseY) {
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

        gui.fill(x, y, x + width, y + height, cfg.colorButtonBorder);
        gui.fill(x + 1, y + 1, x + width - 1, y + height - 1, backgroundColor);

        int textWidth = font.width(this.label);
        int textX = x + (width - textWidth) / 2;
        int textY = y + (height - font.lineHeight) / 2 + 1;

        gui.text(font, this.label, textX, textY, textColor, withShadow);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.visible || !this.enabled) {
            return false;
        }

        if (button == InputUtil.LEFT_MOUSE_BUTTON && this.hovered) {
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
