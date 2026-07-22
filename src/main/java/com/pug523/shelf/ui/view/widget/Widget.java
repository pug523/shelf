package com.pug523.shelf.ui.view;

public interface Widget extends Element {
    default boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers) {
        return false;
    }

    default boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }

    default void mouseReleased(double mouseX, double mouseY, int button) {
    }

    default boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return false;
    }

    default boolean keyPressed(int keycode, int scancode, int modifiers) {
        return false;
    }

    default boolean charTyped(int codepoint, int modifiers) {
        return false;
    }

    default void focusChanged(boolean focus) {
    }
}
