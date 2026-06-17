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
import com.pug523.shelf.gui.model.OptionContext;
import com.pug523.shelf.gui.model.RenderableItem;
import com.pug523.shelf.gui.sound.SoundUtil;
import com.pug523.shelf.gui.widget.OptionWidget;

import net.minecraft.util.Mth;

public final class ConfigInputHandler {

    private final TabTreeController tabs;
    private final ScrollController scrolls;
    private final OptionContextController options;
    private final OptionFocusController focus;
    private final ConfigChangeController changes;

    private boolean isDraggingTabScrollBar = false;
    private boolean isDraggingOptionScrollBar = false;

    public ConfigInputHandler(TabTreeController tabs, ScrollController scrolls, OptionContextController options,
            OptionFocusController focus, ConfigChangeController changes) {
        this.tabs = tabs;
        this.scrolls = scrolls;
        this.options = options;
        this.focus = focus;
        this.changes = changes;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {

        if (button != InputUtil.LEFT_MOUSE_BUTTON) {
            return false;
        }

        if (!layout.isWithinContentArea(mouseY)) {
            return false;
        }

        LayoutConfig cfg = layout.getConfig();
        int yStart = cfg.topBarHeight + 1;
        int height = layout.mainContentHeight - 1;

        if (layout.isMouseOverTabs(mouseX)) {
            int tabScrollBgX = layout.tabAreaWidth - cfg.scrollbarWidth;
            if (mouseX >= tabScrollBgX && mouseX <= layout.tabAreaWidth) {
                isDraggingTabScrollBar = true;
                int totalTabHeight = tabs.totalHeight(cfg);
                int maxHeight = (int) (height * cfg.scrollbarMaxHeightPercent);
                int barHeight = Mth.clamp((int) ((height / (float) totalTabHeight) * height), cfg.scrollbarMinHeight,
                        maxHeight);
                int maxScroll = totalTabHeight - height;
                int barY = yStart + (int) ((scrolls.getTabScroll() / maxScroll) * (height - barHeight));

                boolean forceJump = mouseY < barY || mouseY >= barY + barHeight;
                handleScrollBarDrag(mouseY, layout, true, forceJump);
                return true;
            }
        }

        if (layout.isMouseOverOptions(mouseX)) {
            int optionScrollBgX = layout.descAreaX - cfg.scrollbarWidth;
            if (mouseX >= optionScrollBgX && mouseX <= layout.descAreaX) {
                OptionContext context = options.getContext();
                if (context != null) {
                    isDraggingOptionScrollBar = true;

                    int optionHeight = cfg.optionItemHeight;
                    int extraPadding = 0;
                    List<RenderableItem> items = context.items();
                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).isHeader() && i > 0) {
                            extraPadding += cfg.optionHeaderOffsetY;
                        }
                    }
                    int totalOptionHeight = items.size() * optionHeight + extraPadding + cfg.optionItemStartOffsetY;

                    int maxHeight = (int) (height * cfg.scrollbarMaxHeightPercent);
                    int barHeight = Mth.clamp((int) ((height / (float) totalOptionHeight) * height),
                            cfg.scrollbarMinHeight, maxHeight);
                    int maxScroll = totalOptionHeight - height;
                    int barY = yStart + (int) ((scrolls.getOptionScroll() / maxScroll) * (height - barHeight));

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
        int yStart = cfg.topBarHeight + 1;
        int height = layout.mainContentHeight - 1;

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
                int optionHeight = cfg.optionItemHeight;
                int extraPadding = 0;
                List<RenderableItem> items = context.items();
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isHeader() && i > 0) {
                        extraPadding += cfg.optionHeaderOffsetY;
                    }
                }
                int totalOptionHeight = items.size() * optionHeight + extraPadding + cfg.optionItemStartOffsetY;
                scrolls.setOptionScroll(relativeY * (totalOptionHeight - height), totalOptionHeight, height);
            }
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
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
                widget.mouseReleased(mouseX, mouseY, button);
            }
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double dy, LayoutEngine layout) {
        if (!layout.isWithinContentArea(mouseY)) {
            return false;
        }

        LayoutConfig config = layout.getConfig();
        if (layout.isMouseOverTabs(mouseX)) {
            int totalTabHeight = tabs.totalHeight(config);
            scrolls.scrollTabs(-dy * config.tabScrollSpeed, totalTabHeight, layout.mainContentHeight);
            return true;
        }
        if (layout.isMouseOverOptions(mouseX)) {
            OptionContext context = options.getContext();
            if (context != null) {
                int optionHeight = config.optionItemHeight;
                int extraPadding = 0;
                List<RenderableItem> items = context.items();
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isHeader() && i > 0) {
                        extraPadding += config.optionHeaderOffsetY;
                    }
                }

                int totalOptionHeight = items.size() * optionHeight + extraPadding + config.optionItemStartOffsetY;
                scrolls.scrollOptions(-dy * config.optionScrollSpeed, totalOptionHeight, layout.mainContentHeight);
                return true;
            }
        }

        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY,
            LayoutEngine layout) {
        LayoutConfig cfg = layout.getConfig();
        int height = layout.mainContentHeight - 1;

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
                int optionHeight = cfg.optionItemHeight;
                int extraPadding = 0;
                List<RenderableItem> items = context.items();
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isHeader() && i > 0) {
                        extraPadding += cfg.optionHeaderOffsetY;
                    }
                }
                int totalOptionHeight = items.size() * optionHeight + extraPadding + cfg.optionItemStartOffsetY;
                if (totalOptionHeight > height) {
                    double scrollDelta = (dragY / (double) height) * (totalOptionHeight - height);
                    scrolls.setOptionScroll(scrolls.getOptionScroll() + scrollDelta, totalOptionHeight, height);
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

        if (widget.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            changes.markDirty();
            return true;
        }

        return false;
    }

    public boolean keyPressed(int keycode, int scancode, int modifiers, LayoutEngine layout) {
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

            if (next != current && next >= 0 && next < totalItems) {
                if (current >= 0 && current < totalItems) {
                    RenderableItem prevItem = context.items().get(current);
                    if (prevItem != null && !prevItem.isHeader() && prevItem.widget() != null) {
                        prevItem.widget().focusChanged(false);
                    }
                }

                focus.setFocused(next);

                RenderableItem nextItem = context.items().get(next);
                if (nextItem != null && !nextItem.isHeader() && nextItem.widget() != null) {
                    nextItem.widget().focusChanged(true);
                }

                LayoutConfig cfg = layout.getConfig();
                int extraPadding = 0;
                for (int i = 0; i <= next; i++) {
                    if (context.items().get(i).isHeader() && i > 0) {
                        extraPadding += 12;
                    }
                }

                int itemTopY = cfg.optionItemStartOffsetY + next * cfg.optionItemHeight + extraPadding;
                int itemBottomY = itemTopY + cfg.optionItemHeight;

                int visibleHeight = layout.mainContentHeight - 1;
                double currentScroll = scrolls.getOptionScroll();

                if (itemTopY < currentScroll) {
                    scrolls.setOptionScroll(itemTopY, totalItems * cfg.optionItemHeight + extraPadding, visibleHeight);
                } else if (itemBottomY > currentScroll + visibleHeight) {
                    scrolls.setOptionScroll(itemBottomY - visibleHeight,
                            totalItems * cfg.optionItemHeight + extraPadding, visibleHeight);
                }

                return true;
            }
        }

        boolean result = false;
        for (RenderableItem item : context.items()) {
            OptionWidget<?> widget = item.widget();

            if (widget != null) {
                result |= widget.keyPressed(keycode, scancode, modifiers);
            }
        }
        return result;
    }

    public boolean charTyped(int codepoint, int modifiers) {
        OptionContext context = options.getContext();
        if (context == null) {
            return false;
        }
        boolean result = false;
        for (RenderableItem item : context.items()) {
            OptionWidget<?> widget = item.widget();

            if (widget != null) {
                result |= widget.charTyped(codepoint, modifiers);
            }
        }
        return result;
    }

    private boolean handleTabClick(double mouseX, double mouseY, int modifiers, LayoutEngine layout) {
        List<TabNode> flat = tabs.getFlat();

        for (int i = 0; i < flat.size(); i++) {
            TabNode node = flat.get(i);

            int yPos = layout.getConfig().topBarHeight + layout.getConfig().tabItemStartOffsetY
                    + i * layout.getConfig().tabItemHeight - (int) scrolls.getTabScroll();

            if (mouseY < yPos) {
                continue;
            }

            if (mouseY >= yPos + layout.getConfig().tabItemHeight) {
                continue;
            }

            int toggleX = 10 + node.getDepth() * 10;

            if (node.hasChildren() && mouseX >= toggleX && mouseX <= toggleX + 16) {
                tabs.toggle(node);
            } else {
                OptionContext context = options.getContext();
                int currentFocused = focus.getFocused();
                if (context != null && currentFocused >= 0 && currentFocused < context.items().size()) {
                    RenderableItem prevItem = context.items().get(currentFocused);
                    if (prevItem != null && !prevItem.isHeader() && prevItem.widget() != null) {
                        prevItem.widget().focusChanged(false);
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

        int extraPadding = 0;

        for (int i = 0; i < items.size(); i++) {
            RenderableItem item = items.get(i);

            if (item.isHeader() && i > 0) {
                extraPadding += 12;
            }
            if (item.isHeader()) {
                continue;
            }

            int yPos = layout.getConfig().topBarHeight + layout.getConfig().optionItemStartOffsetY
                    + i * layout.getConfig().optionItemHeight + extraPadding - (int) scrolls.getOptionScroll();

            if (mouseY < yPos) {
                continue;
            }
            if (mouseY >= yPos + layout.getConfig().optionItemHeight) {
                continue;
            }

            int previousFocusedIndex = focus.getFocused();
            if (previousFocusedIndex >= 0 && previousFocusedIndex != i && previousFocusedIndex < items.size()) {
                RenderableItem prevItem = items.get(previousFocusedIndex);
                if (prevItem != null && !prevItem.isHeader() && prevItem.widget() != null) {
                    prevItem.widget().focusChanged(false);
                }
            }

            focus.setFocused(i);
            OptionWidget<?> widget = item.widget();
            if (widget == null) {
                return true;
            }
            widget.focusChanged(true);

            Option<?> option = widget.getOption();
            if (option == null) {
                return true;
            }

            int resetBtnX = layout.descAreaX - layout.getConfig().resetButtonWidth - 6;
            int resetBtnY = yPos + (layout.getConfig().optionItemHeight - 16) / 2 - 1;

            if (mouseX >= resetBtnX && mouseX < resetBtnX + layout.getConfig().resetButtonWidth && mouseY >= resetBtnY
                    && mouseY < resetBtnY + 16) {
                if (option.isPendingModifiedFromDefault()) {
                    option.resetPendingToDefault();
                    SoundUtil.clickSound();

                    changes.markDirty();
                }
                return true;
            }

            if (widget.mouseClicked(mouseX, mouseY, InputUtil.LEFT_MOUSE_BUTTON, modifiers)) {
                changes.markDirty();
                return true;
            }

            return true;
        }

        return false;
    }
}
