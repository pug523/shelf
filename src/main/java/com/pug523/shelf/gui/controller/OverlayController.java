package com.pug523.shelf.gui.controller;

import com.pug523.shelf.gui.widget.overlay.OverlayWidget;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

public class OverlayController {
    private final List<OverlayWidget> overlays = new ArrayList<>();

    public void push(OverlayWidget overlay) {
        if (overlay != null) {
            this.overlays.add(overlay);
        }
    }

    public void pop() {
        if (!this.overlays.isEmpty()) {
            OverlayWidget removed = this.overlays.remove(this.overlays.size() - 1);
            removed.onClose();
        }
    }

    public void closeAbove(OverlayWidget overlay) {
        int index = this.overlays.indexOf(overlay);
        if (index != -1) {
            while (this.overlays.size() - 1 > index) {
                this.pop();
            }
        }
    }

    public void clear() {
        while (!this.overlays.isEmpty()) {
            this.pop();
        }
    }

    public boolean hasActiveOverlay() {
        return !this.overlays.isEmpty();
    }

    public List<OverlayWidget> getOverlays() {
        return this.overlays;
    }

    public @Nullable OverlayWidget getTopOverlay() {
        if (this.overlays.isEmpty()) return null;
        return this.overlays.get(this.overlays.size() - 1);
    }
}
