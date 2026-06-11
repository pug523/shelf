package com.pug523.shelf.gui.input;

import java.util.List;

import com.pug523.shelf.gui.controller.ScrollController;
import com.pug523.shelf.config.Option;
import com.pug523.shelf.gui.TabNode;
import com.pug523.shelf.gui.controller.ConfigChangeController;
import com.pug523.shelf.gui.controller.OptionContextController;
import com.pug523.shelf.gui.controller.OptionFocusController;
import com.pug523.shelf.gui.controller.TabTreeController;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.model.OptionContext;
import com.pug523.shelf.gui.model.RenderableItem;
import com.pug523.shelf.gui.sound.SoundUtil;
import com.pug523.shelf.gui.widget.OptionWidget;

public final class ConfigInputHandler {

    private final TabTreeController tabs;
    private final ScrollController scrolls;
    private final OptionContextController options;
    private final OptionFocusController focus;
    private final ConfigChangeController changes;

    public ConfigInputHandler(TabTreeController tabs, ScrollController scrolls, OptionContextController options,
            OptionFocusController focus, ConfigChangeController changes) {
        this.tabs = tabs;
        this.scrolls = scrolls;
        this.options = options;
        this.focus = focus;
        this.changes = changes;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, LayoutEngine layout) {

        if (button != InputUtil.LEFT_MOUSE_BUTTON) {
            return false;
        }

        if (!layout.isWithinContentArea(mouseY)) {
            return false;
        }

        if (layout.isMouseOverTabs(mouseX)) {
            return handleTabClick(mouseX, mouseY, layout);
        }

        if (layout.isMouseOverOptions(mouseX)) {
            return handleOptionClick(mouseX, mouseY, layout);
        }

        return false;
    }

    private boolean handleTabClick(double mouseX, double mouseY, LayoutEngine layout) {
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
                tabs.select(node);
            }

            return true;
        }

        return false;
    }

    private boolean handleOptionClick(double mouseX, double mouseY, LayoutEngine layout) {

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

            focus.setFocused(i);

            OptionWidget<?> widget = item.widget();

            if (widget == null) {
                return true;
            }

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

            if (widget.mouseClicked(mouseX, mouseY, InputUtil.LEFT_MOUSE_BUTTON)) {

                changes.markDirty();

                return true;
            }

            return true;
        }

        return false;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {

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
        if (layout.isMouseOverTabs(mouseX)) {
            scrolls.scrollTabs(-dy * layout.getConfig().tabScrollSpeed, layout.getHeight(), layout.mainContentHeight);
            return true;
        }
        if (layout.isMouseOverOptions(mouseX)) {
            scrolls.scrollOptions(-dy * layout.getConfig().optionScrollSpeed, layout.getHeight(),
                    layout.mainContentHeight);
            return true;
        }

        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        OptionContext context = options.getContext();

        if (context == null) {
            return false;
        }

        int focusedIndex = focus.getFocused();

        if (focusedIndex < 0) {
            return false;
        }

        if (focusedIndex >= context.items().size()) {
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
}
