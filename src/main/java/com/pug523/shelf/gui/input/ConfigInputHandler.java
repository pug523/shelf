package com.pug523.shelf.gui.input;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;

import com.pug523.shelf.compat.JavaCompat;
import com.pug523.shelf.gui.ConfigScreen;
import com.pug523.shelf.gui.controller.*;
import com.pug523.shelf.gui.TabNode;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.layout.Bounds;
import com.pug523.shelf.gui.layout.OptionRowLayout;
import com.pug523.shelf.gui.model.OptionContext;
import com.pug523.shelf.gui.model.RenderableItem;
import com.pug523.shelf.gui.sound.SoundUtil;
import com.pug523.shelf.gui.widget.option.OptionWidget;

import com.pug523.shelf.gui.widget.SearchBarWidget;
import com.pug523.shelf.gui.widget.overlay.OverlayWidget;

public final class ConfigInputHandler {
    private final ConfigScreen screen;

    private final TabTreeController tabs;
    private final ScrollController scrolls;
    private final OptionContextController options;
    private final OptionFocusController focus;
    private final OverlayController overlays;
    private final ConfigChangeController change;

    private final SearchBarWidget searchBar;

    private boolean isDraggingTabScrollBar = false;
    private boolean isDraggingOptionScrollBar = false;

    public ConfigInputHandler(ConfigScreen screen, TabTreeController tabs, ScrollController scrolls, OptionContextController options,
                              OptionFocusController focus, OverlayController overlays, ConfigChangeController change, SearchBarWidget searchBar) {
        this.screen = screen;
        this.tabs = tabs;
        this.scrolls = scrolls;
        this.options = options;
        this.focus = focus;
        this.overlays = overlays;
        this.change = change;
        this.searchBar = searchBar;
    }

    private List<RenderableItem> getFilteredItems() {
        return this.searchBar.getController().getFilteredOptions();
    }

    private List<TabNode> getFilteredTabs() {
        return this.searchBar.getController().getFilteredTabs();
    }

    private void updateDirty() {
        this.change.setDirty(this.options.getContext().hasPendingChanges());
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        if (overlays.hasActiveOverlay()) {
            List<OverlayWidget> tempOverlays = new ArrayList<>(overlays.getOverlays());
            if (tempOverlays.stream().anyMatch(o -> o.mouseClicked(mouseX, mouseY, button, modifiers, layout))) {
                updateDirty();
                return true;
            }
        }

        if (this.searchBar.mouseClicked(mouseX, mouseY, button, modifiers, layout)) {
            unfocusCurrentOption(layout);
            updateDirty();
            return true;
        }

        if (!layout.isWithinContentArea(mouseY)) {
            updateDirty();
            return false;
        }

        LayoutConfig cfg = layout.getConfig();
        int yStart = layout.tabArea.y;

        if (layout.isMouseOverTabs(mouseX)) {
            if (mouseX >= layout.tabScrollbarTrack.x && mouseX <= layout.tabScrollbarTrack.maxX) {
                isDraggingTabScrollBar = true;
                int totalTabHeight = tabs.totalHeight(cfg);

                Bounds thumb = layout.calculateScrollBarThumb(layout.tabScrollbarTrack, scrolls.getTabScroll(),
                    totalTabHeight);
                int barY = thumb != null ? thumb.y : yStart;
                int barHeight = thumb != null ? thumb.height : cfg.scrollbarMinHeight;

                boolean forceJump = mouseY < barY || mouseY >= barY + barHeight;
                handleScrollBarDrag(mouseY, layout, true, forceJump);
                updateDirty();
                return true;
            }
        }

        if (layout.isMouseOverOptions(mouseX)) {
            if (mouseX >= layout.optionScrollbarTrack.x && mouseX <= layout.optionScrollbarTrack.maxX) {
                OptionContext context = options.getContext();
                if (context != null) {
                    isDraggingOptionScrollBar = true;

                    Bounds thumb = layout.calculateScrollBarThumb(layout.optionScrollbarTrack,
                        scrolls.getOptionScroll(), layout.totalOptionHeight);
                    int barY = thumb != null ? thumb.y : layout.optionScrollbarTrack.y;
                    int barHeight = thumb != null ? thumb.height : cfg.scrollbarMinHeight;

                    boolean forceJump = mouseY < barY || mouseY >= barY + barHeight;
                    handleScrollBarDrag(mouseY, layout, false, forceJump);
                    updateDirty();
                    return true;
                }
            }
        }

        if (layout.isMouseOverTabs(mouseX)) {
            boolean result = handleTabClick(mouseX, mouseY, button, modifiers, layout);
            updateDirty();
            return result;
        }

        if (layout.isMouseOverOptions(mouseX)) {
            boolean result = handleOptionClick(mouseX, mouseY, button, modifiers, layout);
            updateDirty();
            return result;
        }

        updateDirty();
        return false;
    }

    private void handleScrollBarDrag(double mouseY, LayoutEngine layout, boolean isTab, boolean forceJump) {
        LayoutConfig cfg = layout.getConfig();
        int yStart = layout.tabArea.y;
        int height = layout.tabArea.height;

        if (!forceJump) {
            return;
        }

        double relativeY = JavaCompat.clamp((mouseY - yStart) / (double) height, 0.0, 1.0);

        if (isTab) {
            int totalTabHeight = tabs.totalHeight(cfg);
            scrolls.setTabScroll(relativeY * (totalTabHeight - height), totalTabHeight, height);
        } else {
            OptionContext context = options.getContext();
            if (context != null) {
                int contentHeight = 0;
                List<RenderableItem> filteredItems = getFilteredItems();
                if (!layout.optionRows.isEmpty() && !filteredItems.isEmpty()) {
                    OptionRowLayout lastRow = layout.optionRows.get(layout.optionRows.size() - 1);
                    contentHeight = lastRow.rowBounds.maxY + cfg.optionItemStartOffsetY;
                }
                scrolls.setOptionScroll(relativeY * (contentHeight - height), contentHeight, height);
            }
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button, LayoutEngine layout) {
        if (overlays.hasActiveOverlay()) {
            List<OverlayWidget> tempOverlays = new ArrayList<>(overlays.getOverlays());
            tempOverlays.forEach(o -> o.mouseReleased(mouseX, mouseY, button, layout));
            isDraggingTabScrollBar = false;
            isDraggingOptionScrollBar = false;
            updateDirty();
            return;
        }

        if (button == InputConstants.MOUSE_BUTTON_LEFT) {
            isDraggingTabScrollBar = false;
            isDraggingOptionScrollBar = false;
        }

        for (RenderableItem item : getFilteredItems()) {
            if (item.isHeader()) {
                continue;
            }

            OptionWidget<?> widget = item.widget();
            if (widget != null) {
                widget.mouseReleased(mouseX, mouseY, button, layout);
            }
        }
        updateDirty();
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double dx, double dy, LayoutEngine layout) {
        if (overlays.hasActiveOverlay()) {
            List<OverlayWidget> tempOverlays = new ArrayList<>(overlays.getOverlays());
            if (tempOverlays.stream().anyMatch(o -> o.mouseScrolled(mouseX, mouseY, dx, dy, layout))) {
                return true;
            }
        }

        if (!layout.isWithinContentArea(mouseY)) {
            updateDirty();
            return false;
        }

        LayoutConfig config = layout.getConfig();
        if (layout.isMouseOverTabs(mouseX)) {
            int totalTabHeight = tabs.totalHeight(config);
            scrolls.scrollTabs(-dy * config.tabScrollSpeed, totalTabHeight, layout.tabArea.height);
            updateDirty();
            return true;
        }
        if (layout.isMouseOverOptions(mouseX)) {
            OptionContext context = options.getContext();
            if (context != null) {
                int contentHeight = 0;
                List<RenderableItem> filteredItems = getFilteredItems();
                if (!layout.optionRows.isEmpty() && !filteredItems.isEmpty()) {
                    OptionRowLayout lastRow = layout.optionRows.get(layout.optionRows.size() - 1);
                    contentHeight = lastRow.rowBounds.maxY + config.optionItemStartOffsetY;
                }
                scrolls.scrollOptions(-dy * config.optionScrollSpeed, contentHeight, layout.optionArea.height);
                updateDirty();
                return true;
            }
        }

        updateDirty();
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY,
                                LayoutEngine layout) {
        if (overlays.hasActiveOverlay()) {
            List<OverlayWidget> tempOverlays = new ArrayList<>(overlays.getOverlays());
            if (tempOverlays.stream().anyMatch(o -> o.mouseDragged(mouseX, mouseY, button, dragX, dragY, layout))) {
                updateDirty();
                return true;
            }
        }

        LayoutConfig cfg = layout.getConfig();
        int height = layout.tabArea.height;

        if (isDraggingTabScrollBar) {
            int totalTabHeight = tabs.totalHeight(cfg);
            if (totalTabHeight > height) {
                double scrollDelta = (dragY / (double) height) * (totalTabHeight - height);
                scrolls.setTabScroll(scrolls.getTabScroll() + scrollDelta, totalTabHeight, height);
            }
            updateDirty();
            return true;
        }
        if (isDraggingOptionScrollBar) {
            OptionContext context = options.getContext();
            if (context != null) {
                int contentHeight = 0;
                List<RenderableItem> filteredItems = getFilteredItems();
                if (!layout.optionRows.isEmpty() && !filteredItems.isEmpty()) {
                    OptionRowLayout lastRow = layout.optionRows.get(layout.optionRows.size() - 1);
                    contentHeight = lastRow.rowBounds.maxY + cfg.optionItemStartOffsetY;
                }
                if (contentHeight > height) {
                    double scrollDelta = (dragY / (double) height) * (contentHeight - height);
                    scrolls.setOptionScroll(scrolls.getOptionScroll() + scrollDelta, contentHeight, height);
                }
            }
            updateDirty();
            return true;
        }

        List<RenderableItem> filteredItems = getFilteredItems();
        int focusedIndex = focus.getFocused();
        if (focusedIndex < 0 || focusedIndex >= filteredItems.size()) {
            updateDirty();
            return false;
        }

        RenderableItem item = filteredItems.get(focusedIndex);
        if (item.isHeader()) {
            updateDirty();
            return false;
        }

        OptionWidget<?> widget = item.widget();
        if (widget == null) {
            updateDirty();
            return false;
        }

        boolean result = widget.mouseDragged(mouseX, mouseY, button, dragX, dragY, layout);
        updateDirty();
        return result;
    }

    public boolean keyPressed(int keycode, int scancode, int modifiers, LayoutEngine layout) {
        if (overlays.hasActiveOverlay()) {
            List<OverlayWidget> tempOverlays = new ArrayList<>(overlays.getOverlays());
            if (tempOverlays.stream().anyMatch(o -> o.keyPressed(keycode, scancode, modifiers, layout))) {
                updateDirty();
                return true;
            }
        }

        if (this.searchBar.isFocused()) {
            if (keycode == InputConstants.KEY_ESCAPE) {
                this.searchBar.setFocused(false);
                updateDirty();
                return true;
            }
            boolean result = this.searchBar.keyPressed(keycode, scancode, modifiers, layout);
            updateDirty();
            return result;
        }

        OptionWidget<?> focusedWidget = getFocusedWidget();
        if (focusedWidget != null) {
            if (focusedWidget.keyPressed(keycode, scancode, modifiers, layout)) {
                updateDirty();
                return true;
            }
        }

        List<RenderableItem> filteredItems = getFilteredItems();
        if (filteredItems.isEmpty()) {
            updateDirty();
            return false;
        }

        if (keycode == InputConstants.KEY_UP || keycode == InputConstants.KEY_DOWN) {
            int current = focus.getFocused();
            int totalItems = filteredItems.size();
            int next = current;

            do {
                if (keycode == InputConstants.KEY_UP) {
                    next = (next <= 0) ? totalItems - 1 : next - 1;
                } else { // DOWN
                    next = (next >= totalItems - 1) ? 0 : next + 1;
                }
                if (next == current)
                    break;
            } while (filteredItems.get(next).isHeader());

            if (next != current && next < totalItems) {
                if (current >= 0 && current < totalItems) {
                    RenderableItem prevItem = filteredItems.get(current);
                    if (prevItem != null && !prevItem.isHeader() && prevItem.widget() != null) {
                        prevItem.widget().focusChanged(false, layout);
                    }
                }

                focus.setFocused(next);

                RenderableItem nextItem = filteredItems.get(next);
                if (nextItem != null && !nextItem.isHeader() && nextItem.widget() != null) {
                    nextItem.widget().focusChanged(true, layout);
                }

                LayoutConfig cfg = layout.getConfig();
                OptionRowLayout row = layout.optionRows.get(next);

                int itemTopY = row.rowBounds.y;
                int itemBottomY = row.rowBounds.maxY;

                int visibleHeight = layout.optionArea.height;
                double currentScroll = scrolls.getOptionScroll();

                OptionRowLayout lastRow = layout.optionRows.get(layout.optionRows.size() - 1);
                int contentHeight = lastRow.rowBounds.maxY + cfg.optionItemStartOffsetY;

                if (itemTopY < currentScroll) {
                    scrolls.setOptionScroll(itemTopY, contentHeight, visibleHeight);
                } else if (itemBottomY > currentScroll + visibleHeight) {
                    scrolls.setOptionScroll(itemBottomY - visibleHeight, contentHeight, visibleHeight);
                }

                updateDirty();
                return true;
            }
        }

        boolean result = false;
        for (RenderableItem item : filteredItems) {
            OptionWidget<?> widget = item.widget();
            if (widget != null && widget != focusedWidget) {
                result |= widget.keyPressed(keycode, scancode, modifiers, layout);
            }
        }
        updateDirty();

        if (!result && keycode == InputConstants.KEY_ESCAPE) {
            screen.closeOrConfirm();
            result = true;
        }
        return result;
    }

    public boolean charTyped(int codepoint, int modifiers, LayoutEngine layout) {
        if (overlays.hasActiveOverlay()) {
            List<OverlayWidget> tempOverlays = new ArrayList<>(overlays.getOverlays());
            if (tempOverlays.stream().anyMatch(o -> o.charTyped(codepoint, modifiers, layout))) {
                updateDirty();
                return true;
            }
        }

        if (this.searchBar.isFocused()) {
            boolean result = this.searchBar.charTyped(codepoint, modifiers, layout);
            updateDirty();
            return result;
        }

        OptionWidget<?> focusedWidget = getFocusedWidget();
        if (focusedWidget != null) {
            if (focusedWidget.charTyped(codepoint, modifiers, layout)) {
                updateDirty();
                return true;
            }
        }

        List<RenderableItem> filteredItems = getFilteredItems();
        boolean result = false;
        for (RenderableItem item : filteredItems) {
            OptionWidget<?> widget = item.widget();
            if (widget != null && widget != focusedWidget) {
                result |= widget.charTyped(codepoint, modifiers, layout);
            }
        }
        updateDirty();
        return result;
    }

    private OptionWidget<?> getFocusedWidget() {
        List<RenderableItem> filteredItems = getFilteredItems();
        int focusedIndex = focus.getFocused();
        if (focusedIndex >= 0 && focusedIndex < filteredItems.size()) {
            RenderableItem item = filteredItems.get(focusedIndex);
            if (item != null && !item.isHeader()) {
                return item.widget();
            }
        }
        return null;
    }

    private void unfocusCurrentOption(LayoutEngine layout) {
        int currentFocused = focus.getFocused();
        List<RenderableItem> filteredItems = getFilteredItems();
        if (currentFocused >= 0 && currentFocused < filteredItems.size()) {
            RenderableItem prevItem = filteredItems.get(currentFocused);
            if (prevItem != null && !prevItem.isHeader() && prevItem.widget() != null) {
                prevItem.widget().focusChanged(false, layout);
            }
        }
        focus.setFocused(-1);
    }

    private boolean handleTabClick(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        if (button != InputConstants.MOUSE_BUTTON_LEFT) {
            return false;
        }

        List<TabNode> filteredTabs = getFilteredTabs();

        for (int i = 0; i < filteredTabs.size(); i++) {
            TabNode node = filteredTabs.get(i);
            if (i >= layout.tabItemBounds.size()) {
                break;
            }
            Bounds baseBounds = layout.tabItemBounds.get(i);

            int yPos = baseBounds.y - (int) scrolls.getTabScroll();

            if (mouseY < yPos) {
                continue;
            }

            if (mouseY >= yPos + baseBounds.height) {
                continue;
            }

            int toggleX = layout.getConfig().textPaddingX + node.getDepth() * layout.getConfig().tabTreeIndentation;

            if (node.hasChildren() && mouseX >= toggleX && mouseX <= toggleX + 16) {
                tabs.toggle(node);
            } else {
                List<RenderableItem> filteredItems = getFilteredItems();
                int currentFocused = focus.getFocused();
                if (currentFocused >= 0 && currentFocused < filteredItems.size()) {
                    RenderableItem prevItem = filteredItems.get(currentFocused);
                    if (prevItem != null && !prevItem.isHeader() && prevItem.widget() != null) {
                        prevItem.widget().focusChanged(false, layout);
                    }
                }
                focus.setFocused(-1);

                tabs.select(node);
                scrolls.setOptionScroll(0, 0, 0);
            }

            return true;
        }

        return false;
    }

    private boolean handleOptionClick(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        List<RenderableItem> filteredItems = getFilteredItems();
        if (filteredItems.isEmpty()) {
            return false;
        }

        for (int i = 0; i < filteredItems.size(); i++) {
            RenderableItem item = filteredItems.get(i);
            if (i >= layout.optionRows.size()) {
                break;
            }
            OptionRowLayout row = layout.optionRows.get(i);

            if (item.isHeader()) {
                continue;
            }

            int yPos = row.rowBounds.y - (int) scrolls.getOptionScroll();

            if (mouseY < yPos || mouseY >= yPos + row.rowBounds.height) {
                continue;
            }

            int previousFocusedIndex = focus.getFocused();
            if (previousFocusedIndex >= 0 && previousFocusedIndex != i && previousFocusedIndex < filteredItems.size()) {
                RenderableItem prevItem = filteredItems.get(previousFocusedIndex);
                if (prevItem != null && !prevItem.isHeader() && prevItem.widget() != null) {
                    prevItem.widget().focusChanged(false, layout);
                }
            }

            focus.setFocused(i);
            OptionWidget<?> widget = item.widget();
            if (widget == null) {
                return true;
            }
            widget.focusChanged(true, layout);

            Bounds currentResetBounds = new Bounds(row.resetButtonBounds.x, yPos + 4, row.resetButtonBounds.width,
                row.resetButtonBounds.height);

            if (currentResetBounds.contains((int) mouseX, (int) mouseY)) {
                if (widget.isPendingModifiedFromDefault()) {
                    widget.resetPendingToDefault();
                    SoundUtil.clickSound();
                }
                return true;
            }

            return widget.mouseClicked(mouseX, mouseY, button, modifiers, layout);
        }

        return false;
    }
}
