package com.pug523.shelf.gui.overlay;

import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.gui.layout.LayoutEngine;

import net.minecraft.client.gui.Font;

public interface ScreenOverlay {
    void render(Font font, GuiCompat gui, int mouseX, int mouseY, float partialTicks, LayoutEngine layout);

    // Input capture management (Return true to intercept and block underlying layers)
    boolean mouseClicked(double mouseX, double mouseY, int button, LayoutEngine layout);

    boolean mouseReleased(double mouseX, double mouseY, int button, LayoutEngine layout);

    boolean keyPressed(int keycode, int scancode, int modifiers, LayoutEngine layout);

    boolean charTyped(int codepoint, int modifiers, LayoutEngine layout);

    // Determines if the background screen should be dimmed down with a dark overlay.
    default boolean shouldDimBackground() {
        return true;
    }

    // Triggered automatically when the overlay is dismissed or replaced.
    default void onClose() {
    }
}
