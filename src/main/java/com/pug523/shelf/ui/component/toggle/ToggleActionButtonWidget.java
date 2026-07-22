package com.pug523.shelf.ui.component.toggle;

import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.core.geometry.MousePos;
import com.pug523.shelf.core.geometry.Rect;
import com.pug523.shelf.ui.element.RectangleBox;
import com.pug523.shelf.ui.element.Label;
import com.pug523.shelf.ui.element.RoundedBox;
import com.pug523.shelf.ui.component.ActionButtonWidget;
import com.pug523.shelf.ui.Widget;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class ToggleActionButtonWidget implements Widget {
    private final Component onText;
    private final Component offText;
    private boolean value;
    private final Config config;

    public static class Config {
        boolean rounded = true;
    }

    private final ActionButtonWidget actionButtonWidget;

    public ToggleActionButtonWidget(Component onText, Component offText, boolean initialValue, Config config) {
        this.onText = onText;
        this.offText = offText;
        this.value = initialValue;
        this.config = config;

        Label label = new Label(value ? this.onText : this.offText, new Label.Config());
        ActionButtonWidget.Config cfg = new ActionButtonWidget.Config();
        if (config.rounded) {
            RoundedBox roundedBox = new RoundedBox(new RoundedBox.Config());
            this.actionButtonWidget = new ActionButtonWidget(label, roundedBox, cfg, this::onTrigger);
        } else {
            RectangleBox box = new RectangleBox(new RectangleBox.Config());
            this.actionButtonWidget = new ActionButtonWidget(label, box, cfg, this::onTrigger);
        }
    }

    public Config config() {
        return config;
    }

    private void onTrigger() {
        value = !value;
        rebuild();
    }

    private void rebuild() {
        Component newText = value ? this.onText : this.offText;
        actionButtonWidget.label().text(newText);
    }

    @Override
    public void render(Font font, GuiCompat gui, Rect rect, MousePos mousePos) {
        actionButtonWidget.render(font, gui, rect, mousePos);
    }

    @Override
    public boolean mouseClicked(Rect rect, MousePos mousePos, int button, int modifiers) {
        return actionButtonWidget.mouseClicked(rect, mousePos, button, modifiers);
    }

    @Override
    public boolean keyPressed(Rect rect, MousePos mousePos, int keycode, int scancode, int modifiers) {
        return actionButtonWidget.keyPressed(rect, mousePos, keycode, scancode, modifiers);
    }

    @Override
    public void focusChanged(Rect rect, MousePos mousePos, boolean focus) {
        actionButtonWidget.focusChanged(rect, mousePos, focus);
    }
}
