package com.pug523.shelf.gui.input;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.pug523.shelf.gui.controller.ScrollController;
import com.pug523.shelf.config.Option;
import com.pug523.shelf.gui.TabNode;
import com.pug523.shelf.gui.controller.ConfigChangeController;
import com.pug523.shelf.gui.controller.OptionContextController;
import com.pug523.shelf.gui.controller.OptionFocusController;
import com.pug523.shelf.gui.controller.TabTreeController;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.layout.Bounds;
import com.pug523.shelf.gui.layout.OptionRowLayout;
import com.pug523.shelf.gui.model.OptionContext;
import com.pug523.shelf.gui.model.RenderableItem;
import com.pug523.shelf.gui.controller.OverlayController;
import com.pug523.shelf.gui.overlay.ScreenOverlay;
import com.pug523.shelf.gui.sound.SoundUtil;
import com.pug523.shelf.gui.widget.OptionWidget;

import net.minecraft.util.Mth;

public final class ConfigInputHandler {

    private final TabTreeController tabs;
    private final ScrollController scrolls;
    private final OptionContextController options;
    private final OptionFocusController focus;
    private final ConfigChangeController changes;
    private final OverlayController overlays;

    private boolean isDraggingTabScrollBar = false;
    private boolean isDraggingOptionScrollBar = false;

    public ConfigInputHandler(TabTreeController tabs, ScrollController scrolls, OptionContextController options,
                              OptionFocusController focus, ConfigChangeController changes, OverlayController overlays) {
        this.tabs = tabs;
        this.scrolls = scrolls;
        this.options = options;
        this.focus = focus;
        this.changes = changes;
        this.overlays = overlays;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        if (overlays.hasActiveOverlay()) {
            ScreenOverlay overlay = overlays.getActiveOverlay();
            if (overlay != null) {
                overlay.mouseClicked(mouseX, mouseY, button, layout);
                return true;
            }
        }

        // if (button != InputUtil.LEFT_MOUSE_BUTTON) {
        //     return false;
        // }

        if (!layout.isWithinContentArea(mouseY)) {
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
                    return true;
                }
            }
        }

        if (layout.isMouseOverTabs(mouseX)) {
            return handleTabClick(mouseX, mouseY, modifiers, layout);
        }

        if (layout.isMouseOverOptions(mouseX)) {
            return handleOptionClick(mouseX, mouseY, modifiers, layout);
        }

        return false;
    }

    private void handleScrollBarDrag(double mouseY, LayoutEngine layout, boolean isTab, boolean forceJump) {
        LayoutConfig cfg = layout.getConfig();
        int yStart = layout.tabArea.y;
        int height = layout.tabArea.height;

        if (!forceJump) {
            return;
        }

        double relativeY = Mth.clamp((mouseY - yStart) / (double) height, 0.0, 1.0);

        if (isTab) {
            int totalTabHeight = tabs.totalHeight(cfg);
            scrolls.setTabScroll(relativeY * (totalTabHeight - height), totalTabHeight, height);
        } else {
            OptionContext context = options.getContext();
            if (context != null) {
                int contentHeight = 0;
                if (!layout.optionRows.isEmpty()) {
                    OptionRowLayout lastRow = layout.optionRows.get(layout.optionRows.size() - 1);
                    contentHeight = lastRow.rowBounds.maxY + cfg.optionItemStartOffsetY;
                }
                scrolls.setOptionScroll(relativeY * (contentHeight - height), contentHeight, height);
            }
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button, LayoutEngine layout) {
        if (overlays.hasActiveOverlay()) {
            ScreenOverlay overlay = overlays.getActiveOverlay();
            if (overlay != null) {
                overlay.mouseReleased(mouseX, mouseY, button, layout);
                isDraggingTabScrollBar = false;
                isDraggingOptionScrollBar = false;
                return;
            }
        }

        if (button == InputUtil.LEFT_MOUSE_BUTTON) {
            isDraggingTabScrollBar = false;
            isDraggingOptionScrollBar = false;
        }

        OptionContext context = options.getContext();

        if (context == null) {
            return;
        }

        for (RenderableItem item : context.items()) {
            if (item.isHeader()) {
                continue;
            }

            OptionWidget<?> widget = item.widget();

            if (widget != null) {
                widget.mouseReleased(mouseX, mouseY, button, layout);
            }
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double dy, LayoutEngine layout) {
        if (overlays.hasActiveOverlay()) {
            // TODO:
            // overlays.getActiveOverlay().mouseScrolled();
            return false;
        }

        if (!layout.isWithinContentArea(mouseY)) {
            return false;
        }

        LayoutConfig config = layout.getConfig();
        if (layout.isMouseOverTabs(mouseX)) {
            int totalTabHeight = tabs.totalHeight(config);
            scrolls.scrollTabs(-dy * config.tabScrollSpeed, totalTabHeight, layout.tabArea.height);
            return true;
        }
        if (layout.isMouseOverOptions(mouseX)) {
            OptionContext context = options.getContext();
            if (context != null) {
                int contentHeight = 0;
                if (!layout.optionRows.isEmpty()) {
                    OptionRowLayout lastRow = layout.optionRows.get(layout.optionRows.size() - 1);
                    contentHeight = lastRow.rowBounds.maxY + config.optionItemStartOffsetY;
                }
                scrolls.scrollOptions(-dy * config.optionScrollSpeed, contentHeight, layout.optionArea.height);
                return true;
            }
        }

        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY,
                                LayoutEngine layout) {
        if (overlays.hasActiveOverlay()) {
            // TODO:
            // overlays.getActiveOverlay().mouseDragged();
            return false;
        }

        LayoutConfig cfg = layout.getConfig();
        int height = layout.tabArea.height;

        if (isDraggingTabScrollBar) {
            int totalTabHeight = tabs.totalHeight(cfg);
            if (totalTabHeight > height) {
                double scrollDelta = (dragY / (double) height) * (totalTabHeight - height);
                scrolls.setTabScroll(scrolls.getTabScroll() + scrollDelta, totalTabHeight, height);
            }
            return true;
        }
        if (isDraggingOptionScrollBar) {
            OptionContext context = options.getContext();
            if (context != null) {
                int contentHeight = 0;
                if (!layout.optionRows.isEmpty()) {
                    OptionRowLayout lastRow = layout.optionRows.get(layout.optionRows.size() - 1);
                    contentHeight = lastRow.rowBounds.maxY + cfg.optionItemStartOffsetY;
                }
                if (contentHeight > height) {
                    double scrollDelta = (dragY / (double) height) * (contentHeight - height);
                    scrolls.setOptionScroll(scrolls.getOptionScroll() + scrollDelta, contentHeight, height);
                }
            }
            return true;
        }

        OptionContext context = options.getContext();
        if (context == null) {
            return false;
        }

        int focusedIndex = focus.getFocused();
        if (focusedIndex < 0 || focusedIndex >= context.items().size()) {
            return false;
        }

        RenderableItem item = context.items().get(focusedIndex);
        if (item.isHeader()) {
            return false;
        }

        OptionWidget<?> widget = item.widget();
        if (widget == null) {
            return false;
        }

        if (widget.mouseDragged(mouseX, mouseY, button, dragX, dragY, layout)) {
            changes.markDirty();
            return true;
        }

        return false;
    }

    public boolean keyPressed(int keycode, int scancode, int modifiers, LayoutEngine layout) {
        if (overlays.hasActiveOverlay()) {
            ScreenOverlay overlay = overlays.getActiveOverlay();
            if (overlay != null) {
                boolean result = overlay.keyPressed(keycode, scancode, modifiers, layout);
                if (!result && keycode == GLFW.GLFW_KEY_ESCAPE) {
                    overlays.closeActive();
                    result = true;
                }
                return result;
            }
        }

        OptionContext context = options.getContext();
        if (context == null) {
            return false;
        }

        if (keycode == GLFW.GLFW_KEY_UP || keycode == GLFW.GLFW_KEY_DOWN) {
            int current = focus.getFocused();
            int totalItems = context.items().size();
            int next = current;

            do {
                if (keycode == GLFW.GLFW_KEY_UP) {
                    next = (next <= 0) ? totalItems - 1 : next - 1;
                } else { // DOWN
                    next = (next >= totalItems - 1) ? 0 : next + 1;
                }
                if (next == current)
                    break;
            } while (context.items().get(next).isHeader());

            if (next != current && next < totalItems) {
                if (current >= 0 && current < totalItems) {
                    RenderableItem prevItem = context.items().get(current);
                    if (prevItem != null && !prevItem.isHeader() && prevItem.widget() != null) {
                        prevItem.widget().focusChanged(false, layout);
                    }
                }

                focus.setFocused(next);

                RenderableItem nextItem = context.items().get(next);
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

                return true;
            }
        }

        boolean result = false;
        for (RenderableItem item : context.items()) {
            OptionWidget<?> widget = item.widget();

            if (widget != null) {
                result |= widget.keyPressed(keycode, scancode, modifiers, layout);
            }
        }
        return result;
    }

    public boolean charTyped(int codepoint, int modifiers, LayoutEngine layout) {
        if (overlays.hasActiveOverlay()) {
            ScreenOverlay overlay = overlays.getActiveOverlay();
            if (overlay != null) {
                return overlay.charTyped(codepoint, modifiers, layout);
            }
        }

        OptionContext context = options.getContext();
        if (context == null) {
            return false;
        }
        boolean result = false;
        for (RenderableItem item : context.items()) {
            OptionWidget<?> widget = item.widget();

            if (widget != null) {
                result |= widget.charTyped(codepoint, modifiers, layout);
            }
        }
        return result;
    }

    private boolean handleTabClick(double mouseX, double mouseY, int modifiers, LayoutEngine layout) {
        List<TabNode> flat = tabs.getFlat();

        for (int i = 0; i < flat.size(); i++) {
            TabNode node = flat.get(i);
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
                OptionContext context = options.getContext();
                int currentFocused = focus.getFocused();
                if (context != null && currentFocused >= 0 && currentFocused < context.items().size()) {
                    RenderableItem prevItem = context.items().get(currentFocused);
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

    private boolean handleOptionClick(double mouseX, double mouseY, int modifiers, LayoutEngine layout) {
        OptionContext context = options.getContext();
        if (context == null) {
            return false;
        }

        List<RenderableItem> items = context.items();

        for (int i = 0; i < items.size(); i++) {
            RenderableItem item = items.get(i);
            OptionRowLayout row = layout.optionRows.get(i);

            if (item.isHeader()) {
                continue;
            }

            int yPos = row.rowBounds.y - (int) scrolls.getOptionScroll();

            if (mouseY < yPos || mouseY >= yPos + row.rowBounds.height) {
                continue;
            }

            int previousFocusedIndex = focus.getFocused();
            if (previousFocusedIndex >= 0 && previousFocusedIndex != i && previousFocusedIndex < items.size()) {
                RenderableItem prevItem = items.get(previousFocusedIndex);
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

            Option<?> option = widget.getOption();
            if (option == null) {
                return true;
            }

            Bounds currentResetBounds = new Bounds(row.resetButtonBounds.x, yPos + 4, row.resetButtonBounds.width,
                row.resetButtonBounds.height);

            if (currentResetBounds.contains((int) mouseX, (int) mouseY)) {
                if (option.isPendingModifiedFromDefault()) {
                    option.resetPendingToDefault();
                    SoundUtil.clickSound();

                    changes.markDirty();
                }
                return true;
            }

            widget.mouseClicked(mouseX, mouseY, InputUtil.LEFT_MOUSE_BUTTON, modifiers, layout);
            if (option.isPendingModifiedFromActual()) {
                changes.markDirty();
                return true;
            }

            return true;
        }

        return false;
    }
}
