package com.pug523.shelf.ui.screen.controller;

import java.util.List;

import com.pug523.shelf.ui.screen.TabNode;
import com.pug523.shelf.ui.option.OptionWidget;

// TODO: migrate to ConfigScreenViewModel
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
}
