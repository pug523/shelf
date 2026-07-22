package com.pug523.shelf.ui.component.overlay;

import com.pug523.shelf.ui.Widget;

// TODO: refactor
public interface OverlayWidget extends Widget {
    /// Determines if the background screen should be dimmed down with a dark overlay.
    default boolean shouldDimBackground() {
        return false;
    }

    /// Triggered automatically when the overlay is dismissed or replaced.
    default void onClose() {
    }
}
