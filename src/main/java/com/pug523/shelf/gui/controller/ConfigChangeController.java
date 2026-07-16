package com.pug523.shelf.gui.controller;

import java.util.List;

import com.pug523.shelf.gui.ConfigScreen;
import com.pug523.shelf.gui.TabNode;
import com.pug523.shelf.gui.widget.overlay.ConfirmationOverlay;
import com.pug523.shelf.gui.widget.option.OptionWidget;

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

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void apply() {
        roots.stream().flatMap(TabNode::streamAllNodes).flatMap(n -> n.getOptionGroups().stream())
            .flatMap(g -> g.getOptionWidgets().stream())
            .filter(OptionWidget::isPendingModifiedFromActual).forEach(OptionWidget::applyPendingToActual);

        dirty = false;
        onApply.run();
    }

    public void undo() {
        roots.stream().flatMap(TabNode::streamAllNodes).flatMap(n -> n.getOptionGroups().stream())
            .flatMap(g -> g.getOptionWidgets().stream())
            .filter(OptionWidget::isPendingModifiedFromActual).forEach(OptionWidget::discardPending);

        dirty = false;
    }

    public void closeOrConfirm(ConfigScreen screen) {
        if (dirty) {
            OverlayController overlayController = screen.getOverlayController();
            overlayController.clear();
            overlayController.push(new ConfirmationOverlay(o -> screen.close(), o -> overlayController.pop()));
        } else {
            screen.close();
        }
    }
}
