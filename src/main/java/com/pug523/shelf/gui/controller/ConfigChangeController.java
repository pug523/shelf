package com.pug523.shelf.gui.controller;

import java.util.List;

import com.pug523.shelf.config.Option;
import com.pug523.shelf.gui.ConfigScreen;
import com.pug523.shelf.gui.TabNode;
import com.pug523.shelf.gui.overlay.ConfirmationOverlay;
import com.pug523.shelf.gui.widget.OptionWidget;
import net.minecraft.client.gui.screens.Screen;

public class ConfigChangeController {

    private final List<TabNode> roots;
    private final Runnable onApply;
    private boolean dirty = false;

    public ConfigChangeController(List<TabNode> roots, Runnable onApply) {
        this.roots = roots;
        this.onApply = onApply;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void apply() {
        roots.stream().flatMap(TabNode::streamAllNodes).flatMap(n -> n.getOptionGroups().stream())
                .flatMap(g -> g.getOptionWidgets().stream()).map(OptionWidget::getOption)
                .filter(Option::isPendingModifiedFromActual).forEach(Option::applyPendingToActual);

        dirty = false;
        onApply.run();
    }

    public void undo() {
        roots.stream().flatMap(TabNode::streamAllNodes).flatMap(n -> n.getOptionGroups().stream())
                .flatMap(g -> g.getOptionWidgets().stream()).map(OptionWidget::getOption)
                .filter(Option::isPendingModifiedFromActual).forEach(Option::discardPending);

        dirty = false;
    }

    public void closeOrConfirm(ConfigScreen screen) {
        if (dirty) {
            OverlayController overlayController = screen.getOverlayController();
            overlayController.open(new ConfirmationOverlay(screen::close, overlayController::closeActive));
        } else {
            screen.close();
        }
    }
}
