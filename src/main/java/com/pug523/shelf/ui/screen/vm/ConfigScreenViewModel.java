package com.pug523.shelf.ui.screen.vm;

import java.util.List;
import com.pug523.shelf.ui.screen.TabNode;
import com.pug523.shelf.core.config.Profile;

// TODO: implement
public class ConfigScreenViewModel {
    private final List<TabNode> rootTabs;
    private TabNode selectedTab;
    private String searchQuery = "";
    private double scrollAmount = 0;
    private boolean isDirty = false;

    private final Runnable onApply;

    public ConfigScreenViewModel(List<TabNode> roots, List<Profile> profiles, Runnable onApply) {
        this.rootTabs = roots;
        this.onApply = onApply;
        if (!roots.isEmpty()) {
            this.selectedTab = roots.get(0);
        }
    }

    public void selectTab(TabNode node) {
        if (this.selectedTab != node) {
            this.selectedTab = node;
            this.scrollAmount = 0;
            notifyStateChanged();
        }
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query;
        this.scrollAmount = 0;
        notifyStateChanged();
    }

    public void applyChanges() {
        if (onApply != null) onApply.run();
        this.isDirty = false;
    }

    public void undoChanges() {
        this.isDirty = false;
    }

    public boolean isDirty() {
        return isDirty;
    }

    private void notifyStateChanged() {
    }
}
