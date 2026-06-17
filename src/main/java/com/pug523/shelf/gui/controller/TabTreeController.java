package com.pug523.shelf.gui.controller;

import java.util.ArrayList;
import java.util.List;

import com.pug523.shelf.gui.TabNode;
import com.pug523.shelf.gui.layout.LayoutConfig;

public class TabTreeController {

    private final List<TabNode> roots;
    private final List<TabNode> flat = new ArrayList<>();
    private TabNode selected;

    public TabTreeController(List<TabNode> roots) {
        this.roots = roots;
    }

    public void init() {
        rebuildFlat();
        if (!flat.isEmpty()) {
            selected = flat.get(0);
        }
    }

    public void rebuildFlat() {
        flat.clear();
        for (TabNode n : roots) {
            n.flattenVisible(flat, 0);
        }
    }

    public List<TabNode> getFlat() {
        return flat;
    }

    public TabNode getSelected() {
        return selected;
    }

    public void select(TabNode node) {
        this.selected = node;
    }

    public void toggle(TabNode node) {
        node.toggleExpanded();
        rebuildFlat();
    }

    public int totalHeight(LayoutConfig config) {
        return getFlat().size() * config.tabItemHeight + config.tabItemStartOffsetY;
    }
}
