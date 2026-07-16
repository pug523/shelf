package com.pug523.shelf.gui.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.pug523.shelf.gui.TabNode;
import com.pug523.shelf.gui.model.RenderableItem;
import com.pug523.shelf.gui.widget.option.GuiOption;
import com.pug523.shelf.gui.widget.option.OptionWidget;

public class SearchBarController {
    private String query = "";
    private Consumer<String> onQueryChangedCallback;

    private List<RenderableItem> originalItems = new ArrayList<>();
    private List<TabNode> originalTabs = new ArrayList<>();

    private List<RenderableItem> filteredItems = new ArrayList<>();
    private List<TabNode> filteredTabs = new ArrayList<>();

    public void setMasterData(List<RenderableItem> items, List<TabNode> tabs) {
        this.originalItems = items != null ? items : new ArrayList<>();
        this.originalTabs = tabs != null ? tabs : new ArrayList<>();
        rebuildCache();
    }

    public void setQuery(String newQuery) {
        if (newQuery == null) {
            newQuery = "";
        }

        String trimmed = newQuery.trim();
        if (!this.query.equals(trimmed)) {
            this.query = trimmed;

            rebuildCache();

            if (this.onQueryChangedCallback != null) {
                this.onQueryChangedCallback.accept(this.query);
            }
        }
    }

    private void rebuildCache() {
        if (isEmpty()) {
            this.filteredItems = this.originalItems;
            this.filteredTabs = this.originalTabs;
            return;
        }

        this.filteredItems = computeFilterOptions();
        this.filteredTabs = computeFilterTabs();
    }

    public List<RenderableItem> getFilteredOptions() {
        return this.filteredItems;
    }

    public List<TabNode> getFilteredTabs() {
        return this.filteredTabs;
    }

    private List<RenderableItem> computeFilterOptions() {
        List<RenderableItem> filtered = new ArrayList<>();
        List<RenderableItem> currentSection = new ArrayList<>();
        boolean sectionHasMatch = false;

        for (RenderableItem item : this.originalItems) {
            if (item.isHeader()) {
                if (sectionHasMatch) {
                    filtered.addAll(currentSection);
                }
                currentSection.clear();
                sectionHasMatch = false;
                currentSection.add(item);
            } else {
                OptionWidget<?> widget = item.widget();
                if (widget != null && widget.getOption() != null) {
                    if (this.matches(widget.getOption())) {
                        currentSection.add(item);
                        sectionHasMatch = true;
                    }
                }
            }
        }
        if (sectionHasMatch) {
            filtered.addAll(currentSection);
        }

        return filtered;
    }

    private List<TabNode> computeFilterTabs() {
        return this.originalTabs.stream()
            .filter(this::hasMatchingOptionsInTab)
            .collect(Collectors.toList());
    }

    private boolean hasMatchingOptionsInTab(TabNode tab) {
        boolean directMatch = tab.getOptionGroups().stream()
            .flatMap(g -> g.getOptionWidgets().stream())
            .anyMatch(w -> this.matches(w.getOption()));

        return directMatch || tab.getChildren().stream()
            .anyMatch(this::hasMatchingOptionsInTab);
    }

    public String getQuery() {
        return this.query;
    }

    public boolean isEmpty() {
        return this.query.isEmpty();
    }

    public void setOnQueryChanged(Consumer<String> callback) {
        this.onQueryChangedCallback = callback;
    }

    public boolean matches(GuiOption<?> option) {
        if (isEmpty()) {
            return true;
        }

        String lowerQuery = this.query.toLowerCase(Locale.ROOT);

        String name = option.getName().getString().toLowerCase(Locale.ROOT);
        if (name.contains(lowerQuery)) {
            return true;
        }

        if (option.getDescription() != null) {
            String desc = option.getDescription().getString().toLowerCase(Locale.ROOT);
            if (desc.contains(lowerQuery)) {
                return true;
            }
        }

        return false;
    }

    public boolean matches(RenderableItem item) {
        if (isEmpty()) {
            return true;
        }
        if (item.isHeader()) {
            return false;
        }
        if (item.widget() != null && item.widget().getOption() != null) {
            return matches(item.widget().getOption());
        }
        return false;
    }

    public boolean matchesTab(TabNode tab, List<RenderableItem> allItems) {
        if (isEmpty()) {
            return true;
        }

        String tabName = tab.getName().getString().toLowerCase(Locale.ROOT);
        if (tabName.contains(this.query.toLowerCase(Locale.ROOT))) {
            return true;
        }

        for (RenderableItem item : allItems) {
            if (item.widget() != null && item.widget().getOption() != null) {
                GuiOption<?> opt = item.widget().getOption();
                if (matches(opt)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void clear() {
        setQuery("");
    }
}
