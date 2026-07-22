package com.pug523.shelf.ui.component;

import com.mojang.blaze3d.platform.InputConstants;
import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.common.compat.JavaCompat;
import com.pug523.shelf.core.geometry.MousePos;
import com.pug523.shelf.core.geometry.Rect;
import com.pug523.shelf.ui.Element;
import com.pug523.shelf.ui.Widget;
import com.pug523.shelf.ui.element.Label;
import net.minecraft.client.gui.Font;

import java.util.List;

public class ActionButtonWidget implements Widget {
    private final Label label;
    private final Element backgroundBox;
    private final List<Runnable> listeners;

    private final Config config;

    public static class Config {
        public boolean enabled = true;
        public boolean triggerOnClick = true;
        public boolean triggerOnEnterWithFocus = true;
    }

    private boolean focus = false;

    public ActionButtonWidget(Label label, Element backgroundBox, Config config, List<Runnable> listeners) {
        this.label = label;
        this.backgroundBox = backgroundBox;
        this.config = config;
        this.listeners = listeners;
    }

    public ActionButtonWidget(Label label, Element backgroundBox, Config config, Runnable listener) {
        this(label, backgroundBox, config, JavaCompat.listOf(listener));
    }

    public Label label() {
        return label;
    }

    public Element backgroundBox() {
        return backgroundBox;
    }

    public Config config() {
        return config;
    }

    private void trigger() {
        listeners.forEach(Runnable::run);
    }

    @Override
    public void render(Font font, GuiCompat gui, Rect rect, MousePos mousePos) {
        backgroundBox.render(font, gui, rect, mousePos);
        label.render(font, gui, rect, mousePos);
    }

    @Override
    public boolean mouseClicked(Rect rect, MousePos mousePos, int button, int modifiers) {
        if (config.enabled &&
            config.triggerOnClick &&
            mousePos.isHovering(rect) &&
            button == InputConstants.MOUSE_BUTTON_LEFT) {
            trigger();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(Rect rect, MousePos mousePos, int keycode, int scancode, int modifiers) {
        boolean isEnter = keycode == InputConstants.KEY_RETURN || keycode == InputConstants.KEY_NUMPADENTER;
        if (config.enabled &&
            config.triggerOnEnterWithFocus &&
            focus &&
            isEnter) {
            trigger();
            return true;
        }
        return false;
    }

    @Override
    public void focusChanged(Rect rect, MousePos mousePos, boolean focus) {
        this.focus = focus;
    }
}
