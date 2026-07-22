package com.pug523.shelf.ui.option;

import com.pug523.shelf.common.compat.ComponentCompat;
import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.ui.layout.LayoutConfig;
import com.pug523.shelf.ui.layout.LayoutEngine;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

// TODO: refactor
public class ToggleActionButtonOptionWidget extends ToggleOptionWidget {
    private final Component onText;
    private final Component offText;

    private final ActionButtonWidget buttonDelegate;
    private boolean cachedPendingValue;

    public ToggleActionButtonOptionWidget(GuiOption<Boolean> option) {
        this(option, "true", "false");
    }

    public ToggleActionButtonOptionWidget(GuiOption<Boolean> option, String onText, String offText) {
        this(option, ComponentCompat.literal(onText).withStyle(ChatFormatting.GREEN), ComponentCompat.literal(offText).withStyle(ChatFormatting.RED));
    }

    public ToggleActionButtonOptionWidget(GuiOption<Boolean> option, Component onText, Component offText) {
        super(option);
        this.onText = onText;
        this.offText = offText;
        this.buttonDelegate = new ActionButtonWidget(offText, btn -> this.toggle());
        this.cachedPendingValue = false;

        this.buttonDelegate.setSilent(true);
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX, int mouseY) {
        LayoutConfig cfg = layout.getConfig();

        boolean pendingValue = getPendingValue();
        if (pendingValue != cachedPendingValue) {
            this.buttonDelegate.setLabel(pendingValue ? onText : offText);
            cachedPendingValue = pendingValue;
        }

        int btnX = x + width - cfg.toggleButtonWidth - layout.optionWidgetRightMargin;
        int btnY = y + (height - cfg.toggleButtonHeight) / 2;

        this.buttonDelegate.render(font, gui, layout, btnX, btnY, cfg.toggleButtonWidth, cfg.toggleButtonHeight, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        return this.buttonDelegate.mouseClicked(mouseX, mouseY, button, modifiers, layout);
    }
}
