package com.pug523.shelf.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import net.minecraft.network.chat.Component;

public class TabNode {
    private final Component name;
    private final List<OptionGroup> optionGroups = new ArrayList<>();
    private final List<TabNode> children = new ArrayList<>();

    // Set dynamically when calculating visibility layout.
    private int depth = 0;
    private boolean expanded = true;

    public TabNode(Component name) {
        this.name = name;
    }

    public Component getName() {
        return this.name;
    }

    // TODO: add flatten options without option group

    public List<OptionGroup> getOptionGroups() {
        return this.optionGroups;
    }

    public List<TabNode> getChildren() {
        return this.children;
    }

    public int getDepth() {
        return this.depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isExpanded() {
        return this.expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public void toggleExpanded() {
        this.expanded = !this.expanded;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    // Fluent API helper to easily add sub-categories.
    public TabNode addNode(TabNode child) {
        this.children.add(child);
        return this;
    }

    // Fluent API helper to add option groupings directly to this node.
    public TabNode addGroup(OptionGroup group) {
        this.optionGroups.add(group);
        return this;
    }

    // Recursively flattens only the visible branches into a list for the UI renderer.
    public void flattenVisible(List<TabNode> list, int currentDepth) {
        this.depth = currentDepth;
        list.add(this);
        if (this.expanded) {
            for (TabNode child : children) {
                child.flattenVisible(list, currentDepth + 1);
            }
        }
    }

    public Stream<TabNode> streamAllNodes() {
        return Stream.concat(Stream.of(this), this.children.stream().flatMap(TabNode::streamAllNodes));
    }
}
