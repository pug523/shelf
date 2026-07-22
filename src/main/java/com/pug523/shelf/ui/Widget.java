package com.pug523.shelf.ui;

import com.pug523.shelf.core.geometry.MousePos;
import com.pug523.shelf.core.geometry.Rect;

public interface Widget extends Element {
    default boolean mouseClicked(Rect rect, MousePos mousePos, int button, int modifiers) {
        return false;
    }

    default boolean mouseDragged(Rect rect, MousePos mousePos, int button, double dragX, double dragY) {
        return false;
    }

    default void mouseReleased(Rect rect, MousePos mousePos, int button) {
    }

    default boolean mouseScrolled(Rect rect, MousePos mousePos, double scrollX, double scrollY) {
        return false;
    }

    default boolean keyPressed(Rect rect, MousePos mousePos, int keycode, int scancode, int modifiers) {
        return false;
    }

    default boolean charTyped(Rect rect, MousePos mousePos, int codepoint, int modifiers) {
        return false;
    }

    default void focusChanged(Rect rect, MousePos mousePos, boolean focus) {
    }
}
