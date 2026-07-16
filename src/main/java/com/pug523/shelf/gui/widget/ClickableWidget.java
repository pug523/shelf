package com.pug523.shelf.gui.widget;

import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.gui.layout.LayoutEngine;

import net.minecraft.client.gui.Font;

public interface ClickableWidget {
    void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX,
                int mouseY);

    default void rebuildWidget(LayoutEngine layout) {
    }

    default boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        return false;
    }

    default boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY,
                                 LayoutEngine layout) {
        return false;
    }

    default void mouseReleased(double mouseX, double mouseY, int button, LayoutEngine layout) {
    }

    default boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY, LayoutEngine layout) {
        return false;
    }

    default boolean keyPressed(int keycode, int scancode, int modifiers, LayoutEngine layout) {
        return false;
    }

    default boolean charTyped(int codepoint, int modifiers, LayoutEngine layout) {
        return false;
    }

    default void focusChanged(boolean focus, LayoutEngine layout) {
    }
}
