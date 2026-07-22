package com.pug523.shelf.ui.screen.renderer;

import java.util.List;

import com.pug523.shelf.ShelfConfigManager;
import com.pug523.shelf.common.compat.ComponentCompat;
import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.common.compat.IdentifierCompat;
import com.pug523.shelf.common.compat.ProfilerCompat;
import com.pug523.shelf.ui.render.RenderUtil;
import com.pug523.shelf.ui.screen.ConfigScreen;
import com.pug523.shelf.ui.screen.TabNode;
import com.pug523.shelf.ui.layout.LayoutConfig;
import com.pug523.shelf.ui.layout.LayoutEngine;
import com.pug523.shelf.gui.layout.Bounds;
import com.pug523.shelf.gui.layout.OptionRowLayout;
import com.pug523.shelf.ui.model.OptionContext;
import com.pug523.shelf.ui.model.RenderableItem;
import com.pug523.shelf.ui.option.GuiOption;
import com.pug523.shelf.ui.component.overlay.OverlayWidget;
import com.pug523.shelf.ui.screen.controller.*;
import com.pug523.shelf.ui.text.TextUtil;
import com.pug523.shelf.ui.option.OptionWidget;
import com.pug523.shelf.ui.component.SearchBarWidget;

import net.minecraft.ChatFormatting;
//#if MC >= 12106
import net.minecraft.client.renderer.RenderPipelines;
//#elseif MC >= 12102
//$$ import net.minecraft.client.renderer.RenderType;
//#elseif MC >= 11500
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//#endif
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.profiling.ProfilerFiller;

// TODO: refactor
public class ConfigScreenRenderer {
    private static final String RESET_BUTTON_PATH = "textures/gui/reset_button.png";
    private static final Identifier RESET_BUTTON_TEXTURE = IdentifierCompat.ofShelf(RESET_BUTTON_PATH);

    private static final int INVALID_MOUSE_POSITION = -8000;

    public void render(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, int mouseX, int mouseY, float partialTick, TabTreeController tabs, OptionContext context, OptionFocusController focus, ScrollController scrolls, ConfigChangeController change, OverlayController overlay, SearchBarController searchBar) {
        ProfilerFiller profiler = ProfilerCompat.getProfiler();
        profiler.push("shelf_screen_render");

        boolean hasOverlay = overlay.hasActiveOverlay();
        int renderMouseX = hasOverlay ? INVALID_MOUSE_POSITION : mouseX;
        int renderMouseY = hasOverlay ? INVALID_MOUSE_POSITION : mouseY;

        boolean dirty = change.isDirty();
        screen.getFooterButtons().get(0).setEnabled(dirty); // undoButton
        screen.getFooterButtons().get(1).setEnabled(dirty); // applyButton

        LayoutConfig cfg = layout.getConfig();

        profiler.push("filter options");
        List<RenderableItem> filteredItems = searchBar.getFilteredOptions();
        profiler.popPush("filter tabs");
        List<TabNode> filteredTabs = searchBar.getFilteredTabs();

        profiler.popPush("perform dynamic layout");
        layout.performDynamicLayout(filteredTabs, filteredItems, cfg);

        profiler.popPush("panels");
        renderPanels(gui, screen, layout, cfg);
        profiler.popPush("header");
        renderHeader(gui, screen, layout, cfg, renderMouseX, renderMouseY);
        profiler.popPush("footer");
        renderFooter(gui, screen, layout, cfg, renderMouseX, renderMouseY);
        profiler.popPush("tabs");
        renderTabs(gui, screen, layout, cfg, renderMouseX, renderMouseY, tabs, filteredTabs, scrolls);
        profiler.popPush("options");
        renderOptions(gui, screen, layout, cfg, renderMouseX, renderMouseY, filteredItems, focus, scrolls);
        profiler.popPush("description");
        renderDescription(gui, screen, layout, cfg, filteredItems, focus);
        profiler.popPush("overlays");
        renderOverlays(gui, screen, layout, cfg, mouseX, mouseY, partialTick, overlay);
        profiler.pop();

        // For debug
        if (ShelfConfigManager.getConfig().debugBoolean) {
            profiler.push("debug");
            RenderUtil.renderCircle(gui, 300.0f, 150.0f, 20.0f, 0xC000FF00);
            profiler.pop();
        }

        profiler.pop();
    }

    public void rebuildWidgets(OptionContext context, LayoutEngine layout) {
        // TODO: add rebuild tabs, descriptions, overlays, etc.
        // tabs.getFlat().forEach(tab -> tab.rebuildWidget(layout));
        context.streamOptionWidgets().forEach(widget -> widget.rebuildWidget(layout));
    }

    private void renderPanels(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg) {
        gui.fill(0, 0, screen.width, screen.height, cfg.colorScreenBaseBackground);
        gui.fill(layout.tabArea.x, layout.tabArea.y, layout.tabArea.maxX, layout.tabArea.maxY, cfg.colorTabPanelBackground);
        gui.fill(layout.optionArea.x, layout.optionArea.y, layout.optionArea.maxX, layout.optionArea.maxY, cfg.colorOptionPanelBackground);
        gui.fill(layout.descArea.x, layout.descArea.y, layout.descArea.maxX, layout.descArea.maxY, cfg.colorDescriptionPanelBackground);
    }

    private void renderHeader(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg, int mouseX, int mouseY) {
        gui.fill(layout.headerArea.x, layout.headerArea.y, layout.headerArea.maxX, layout.headerArea.maxY, cfg.colorHeaderBackground);
        int textY = (layout.headerArea.height - screen.getFont().lineHeight) / 2 + 1;
        gui.text(screen.getFont(), screen.getTitle(), cfg.textPaddingX, textY, cfg.colorTextPrimary, false);

        SearchBarWidget searchBar = screen.getSearchBarWidget();
        int searchBarX = layout.headerArea.maxX - cfg.searchBarWidth - cfg.textPaddingX;
        int searchBarHeight = screen.getFont().lineHeight + 4;
        searchBar.render(screen.getFont(), gui, layout, searchBarX, textY, cfg.searchBarWidth, searchBarHeight, mouseX, mouseY);
    }

    private void renderFooter(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg, int mouseX, int mouseY) {
        gui.fill(layout.footerArea.x, layout.footerArea.y, layout.footerArea.maxX, layout.footerArea.maxY, cfg.colorFooterBackground);

        List<ActionButtonWidget> footerButtons = screen.getFooterButtons();
        if (footerButtons == null || layout.footerButtonBounds.size() != footerButtons.size()) return;

        for (int i = 0; i < footerButtons.size(); i++) {
            Bounds btnBounds = layout.footerButtonBounds.get(i);
            gui.enableScissor(btnBounds.x, btnBounds.y, btnBounds.maxX, btnBounds.maxY);
            footerButtons.get(i).render(screen.getFont(), gui, layout, btnBounds.x, btnBounds.y, btnBounds.width, btnBounds.height, mouseX, mouseY);
            gui.disableScissor();
        }
    }

    private void renderTabs(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg, int mouseX, int mouseY, TabTreeController tabs, List<TabNode> filteredTabs, ScrollController scrolls) {
        gui.enableScissor(layout.tabArea.x, layout.tabArea.y, layout.tabArea.maxX, layout.tabArea.maxY);

        double tabScroll = scrolls.getTabScroll();
        int textOffset = (cfg.tabItemHeight - screen.getFont().lineHeight) / 2;

        for (int i = 0; i < filteredTabs.size(); i++) {
            if (i >= layout.tabItemBounds.size())
                break;

            TabNode node = filteredTabs.get(i);
            Bounds scrolledBounds = layout.getScrolledTabBounds(i, tabScroll);

            if (scrolledBounds.y + scrolledBounds.height < layout.tabArea.y + cfg.tabScissorClipPaddingY || scrolledBounds.y > screen.height) {
                continue;
            }

            boolean hovered = mouseX >= layout.tabArea.x && mouseX < layout.tabArea.maxX
                && mouseY >= scrolledBounds.y && mouseY < scrolledBounds.y + scrolledBounds.height;

            if (hovered) {
                gui.fill(0, scrolledBounds.y, layout.tabArea.width, scrolledBounds.y + scrolledBounds.height,
                    cfg.colorItemHoverBackground);
            }

            int color = (node == tabs.getSelected()) ? cfg.colorItemSelectedText : cfg.colorItemUnselectedText;
            if (node == tabs.getSelected()) {
                gui.fill(0, scrolledBounds.y, layout.tabArea.width, scrolledBounds.y + scrolledBounds.height,
                    cfg.colorItemSelectedBackground);
            }

            int itemX = cfg.textPaddingX + (node.getDepth() * cfg.tabTreeIndentation);
            int centerY = scrolledBounds.y + scrolledBounds.height / 2 + cfg.engineTextHeightOffset;
            if (node.hasChildren()) {
                if (node.isExpanded()) {
                    RenderUtil.renderDownwardArrow(gui, itemX + cfg.tabArrowOffsetX, centerY - cfg.tabArrowDownwardOffsetY, color);
                } else {
                    RenderUtil.renderRightwardArrow(gui, itemX + cfg.tabArrowOffsetX + cfg.tabArrowRightwardOffsetX, centerY - cfg.tabArrowRightwardOffsetY, color);
                }
            }
            gui.text(screen.getFont(), node.getName(), itemX + cfg.tabTextOffsetX, scrolledBounds.y + textOffset, color, false);
        }
        gui.disableScissor();

        renderScrollbar(gui, layout, layout.tabScrollbarTrack, tabScroll, layout.totalTabHeight, cfg);
    }

    private void renderOptions(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg, int mouseX, int mouseY, List<RenderableItem> filteredItems, OptionFocusController focus, ScrollController scrolls) {
        gui.enableScissor(layout.optionArea.x, layout.optionArea.y, layout.optionArea.maxX, layout.optionArea.maxY);

        boolean hasValidOptions = filteredItems.stream().anyMatch(item -> !item.isHeader());
        if (filteredItems.isEmpty() || !hasValidOptions) {
            Component noResultsText = TextUtil.guiText("no_results");
            int textWidth = ComponentCompat.width(screen.getFont(), noResultsText);
            int centerX = layout.optionArea.x + (layout.optionArea.width - textWidth) / 2;
            int centerY = layout.optionArea.y + (layout.optionArea.height - screen.getFont().lineHeight) / 2 + 1;
            gui.text(screen.getFont(), noResultsText, centerX, centerY, cfg.colorTextDisabled, false);
            gui.disableScissor();
            return;
        }

        double optionScroll = scrolls.getOptionScroll();
        int focusedIdx = focus.getFocused();

        RenderableItem focusedItem = null;
        OptionRowLayout focusedRow = null;

        for (int i = 0; i < filteredItems.size(); i++) {
            if (i >= layout.optionRows.size()) break;

            RenderableItem item = filteredItems.get(i);
            OptionRowLayout row = layout.optionRows.get(i);

            Bounds scrolledRow = layout.getScrolledOptionBounds(row.rowBounds, optionScroll);
            if (scrolledRow.y + scrolledRow.height < layout.optionArea.y + cfg.tabScissorClipPaddingY || scrolledRow.y > layout.optionArea.maxY) {
                continue;
            }

            int textY = layout.getScrolledTextY(row.textY, optionScroll);

            if (item.isHeader()) {
                gui.text(screen.getFont(), item.text(), row.textX, textY, cfg.colorTextMuted, false);
                continue;
            }

            OptionWidget<?> widget = item.widget();
            GuiOption<?> option = widget != null ? widget.getOption() : null;
            if (widget == null || option == null || row.resetButtonBounds == null) continue;

            boolean selected = i == focusedIdx;

            if (selected) {
                focusedItem = item;
                focusedRow = row;
                continue;
            }

            boolean hovered = mouseX >= layout.optionArea.x + 1 && mouseX < layout.optionArea.x + scrolledRow.width && mouseY >= scrolledRow.y && mouseY < scrolledRow.y + scrolledRow.height;

            if (hovered) {
                gui.fill(layout.optionArea.x, scrolledRow.y, layout.optionArea.x + scrolledRow.width, scrolledRow.y + scrolledRow.height, cfg.colorItemHoverBackground);
            }

            int color = cfg.colorItemUnselectedText;
            gui.text(screen.getFont(), option.getName(), row.textX, textY, color, false);

            gui.enableScissor(layout.optionArea.x, layout.optionArea.y, layout.optionArea.maxX, layout.optionArea.maxY);
            widget.render(screen.getFont(), gui, layout, scrolledRow.x, scrolledRow.y, scrolledRow.width, scrolledRow.height, mouseX, mouseY);
            gui.disableScissor();

            // Reset Button Area
            Bounds scrolledReset = layout.getScrolledResetButtonBounds(row.resetButtonBounds, optionScroll);
            boolean resetHovered = scrolledReset.contains(mouseX, mouseY);
            boolean canReset = widget.isPendingModifiedFromDefault();

            int resetBg = resetHovered && canReset ? cfg.colorResetButtonBgHover : cfg.colorResetButtonBgDefault;
            gui.fill(scrolledReset.x, scrolledReset.y, scrolledReset.maxX, scrolledReset.maxY, resetBg);

            int rx = scrolledReset.x + (scrolledReset.width - cfg.resetIconSize) / 2;
            int ry = scrolledReset.y + (scrolledReset.height - cfg.resetIconSize) / 2;

            renderResetIcon(gui, rx, ry, hovered, canReset, cfg);
        }

        if (focusedItem != null) {
            Bounds scrolledRow = layout.getScrolledOptionBounds(focusedRow.rowBounds, optionScroll);
            if (!(scrolledRow.y + scrolledRow.height < layout.optionArea.y + cfg.tabScissorClipPaddingY || scrolledRow.y > layout.optionArea.maxY)) {
                int textY = layout.getScrolledTextY(focusedRow.textY, optionScroll);
                OptionWidget<?> widget = focusedItem.widget();
                GuiOption<?> option = widget.getOption();

                gui.fill(layout.optionArea.x, scrolledRow.y, layout.optionArea.x + scrolledRow.width, scrolledRow.y + scrolledRow.height, cfg.colorItemSelectedBackground);
                gui.text(screen.getFont(), option.getName(), focusedRow.textX, textY, cfg.colorItemSelectedText, false);

                gui.enableScissor(layout.optionArea.x, layout.optionArea.y, layout.optionArea.maxX, layout.optionArea.maxY);
                widget.render(screen.getFont(), gui, layout, scrolledRow.x, scrolledRow.y, scrolledRow.width, scrolledRow.height, mouseX, mouseY);
                gui.disableScissor();

                Bounds scrolledReset = layout.getScrolledResetButtonBounds(focusedRow.resetButtonBounds, optionScroll);
                boolean resetHovered = scrolledReset.contains(mouseX, mouseY);
                boolean canReset = widget.isPendingModifiedFromDefault();

                int resetBg = resetHovered && canReset ? cfg.colorResetButtonBgHover : cfg.colorResetButtonBgDefault;
                gui.fill(scrolledReset.x, scrolledReset.y, scrolledReset.maxX, scrolledReset.maxY, resetBg);

                int rx = scrolledReset.x + (scrolledReset.width - cfg.resetIconSize) / 2;
                int ry = scrolledReset.y + (scrolledReset.height - cfg.resetIconSize) / 2;

                renderResetIcon(gui, rx, ry, true, canReset, cfg);
            }
        }

        gui.disableScissor();

        renderScrollbar(gui, layout, layout.optionScrollbarTrack, optionScroll, layout.totalOptionHeight, cfg);
    }

    private void renderResetIcon(GuiCompat gui, int rx, int ry, boolean hovered, boolean canReset, LayoutConfig cfg) {
        float alpha = canReset ? (hovered ? cfg.resetIconAlphaHover : cfg.resetIconAlphaDefault) : cfg.resetIconAlphaDisabled;
        float r = canReset ? cfg.resetIconActiveR : cfg.resetIconInactiveR;
        float g = canReset ? cfg.resetIconActiveG : cfg.resetIconInactiveG;
        float b = canReset ? cfg.resetIconActiveB : cfg.resetIconInactiveB;

        //#if MC >= 12106
        gui.blit(RenderPipelines.GUI_TEXTURED, RESET_BUTTON_TEXTURE, rx, ry, 0.0f, 0.0f, cfg.resetIconSize, cfg.resetIconSize, cfg.resetIconSize, cfg.resetIconSize, colorFromArgbFloat(alpha, r, g, b));
        //#elseif MC >= 12102
        //$$ gui.blit(RenderType::guiTextured, RESET_BUTTON_TEXTURE, rx, ry, 0.0f, 0.0f, cfg.resetIconSize, cfg.resetIconSize,
        //$$     cfg.resetIconSize, cfg.resetIconSize, colorFromArgbFloat(alpha, r, g, b));
        //#else
        //$$ gui.blit(RESET_BUTTON_TEXTURE, rx, ry, 0.0f, 0.0f, cfg.resetIconSize, cfg.resetIconSize,
        //$$     cfg.resetIconSize, cfg.resetIconSize, r, g, b, alpha);
        //#endif
    }

    private void renderScrollbar(GuiCompat gui, LayoutEngine layout, Bounds track, double scroll, int contentHeight, LayoutConfig cfg) {
        Bounds thumb = layout.calculateScrollBarThumb(track, scroll, contentHeight);
        if (thumb != null) {
            gui.fill(track.x, track.y, track.maxX, track.maxY, cfg.colorScrollBarTrack);
            gui.fill(thumb.x, thumb.y, thumb.maxX, thumb.maxY, cfg.colorScrollBarThumb);
        }
    }

    private void renderDescription(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg, List<RenderableItem> items, OptionFocusController focus) {
        int idx = focus.getFocused();
        int x = layout.descArea.x + cfg.descTextOffsetX;
        int y = layout.descArea.y + cfg.descTextOffsetY;

        if (idx < 0 || idx >= items.size() || items.get(idx).isHeader() || items.get(idx).widget() == null) {
            gui.text(screen.getFont(), TextUtil.guiText("select_an_option"), x, y, cfg.colorTextDisabled, false);
            return;
        }

        GuiOption<?> option = items.get(idx).widget().getOption();
        int wrapW = screen.width - layout.descArea.x - cfg.descTextRightPadding;

        gui.text(screen.getFont(), option.getName().copy().withStyle(ChatFormatting.BOLD), x, y, cfg.colorTextPrimary, false);
        gui.textWithWordWrap(screen.getFont(), option.getDescription(), x, y + screen.getFont().lineHeight + cfg.descTitleSpacingY, wrapW, cfg.colorTextSecondary);
    }

    private void renderOverlays(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg, int mouseX, int mouseY, float partialTick, OverlayController overlay) {
        if (!overlay.hasActiveOverlay()) {
            return;
        }
        List<OverlayWidget> overlays = overlay.getOverlays();
        if (overlays.stream().anyMatch(OverlayWidget::shouldDimBackground)) {
            gui.fill(0, 0, screen.width, screen.height, cfg.colorOverlayDimBackground);
        }
        overlays.forEach(o -> o.render(screen.getFont(), gui, layout, 0, 0, screen.width, screen.height, mouseX, mouseY));
    }

    private static int colorFromArgbFloat(float a, float r, float g, float b) {
        return (int) (a * 255.0f) << 24 | (int) (r * 255.0f) << 16 | (int) (g * 255.0f) << 8 | (int) (b * 255.0f);
    }
}
