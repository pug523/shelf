package com.pug523.shelf.gui.controller;

import com.pug523.shelf.gui.overlay.ScreenOverlay;
import org.jspecify.annotations.Nullable;

public class OverlayController {
    private @Nullable ScreenOverlay activeOverlay = null;

    public void open(@Nullable ScreenOverlay overlay) {
        if (this.activeOverlay != null) {
            this.activeOverlay.onClose();
        }
        this.activeOverlay = overlay;
    }

    public void closeActive() {
        this.open(null);
    }

    public boolean hasActiveOverlay() {
        return this.activeOverlay != null;
    }

    public @Nullable ScreenOverlay getActiveOverlay() {
        return this.activeOverlay;
    }
}
