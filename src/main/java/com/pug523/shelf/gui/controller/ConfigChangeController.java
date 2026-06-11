package com.pug523.shelf.gui.controller;

import java.util.List;

import com.pug523.shelf.config.Option;
import com.pug523.shelf.gui.TabNode;
import com.pug523.shelf.gui.widget.OptionWidget;

public class ConfigChangeController {

    private final List<TabNode> roots;
    private final Runnable onApply;
    private boolean dirty;

    public ConfigChangeController(List<TabNode> roots, Runnable onApply) {
        this.roots = roots;
        this.onApply = onApply;
    }

    public void init() {
        this.dirty = false;
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
}
