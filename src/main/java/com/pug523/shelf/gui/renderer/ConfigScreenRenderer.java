package com.pug523.shelf.gui.renderer;

import java.util.List;

import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.compat.IdentifierCompat;
import com.pug523.shelf.compat.ProfilerCompat;
import com.pug523.shelf.config.Option;
import com.pug523.shelf.gui.ConfigScreen;
import com.pug523.shelf.gui.TabNode;
import com.pug523.shelf.gui.controller.ConfigChangeController;
import com.pug523.shelf.gui.controller.OptionFocusController;
import com.pug523.shelf.gui.controller.ScrollController;
import com.pug523.shelf.gui.controller.TabTreeController;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.layout.Bounds;
import com.pug523.shelf.gui.layout.OptionRowLayout;
import com.pug523.shelf.gui.model.OptionContext;
import com.pug523.shelf.gui.model.RenderableItem;
import com.pug523.shelf.gui.controller.OverlayController;
import com.pug523.shelf.gui.overlay.ScreenOverlay;
import com.pug523.shelf.gui.text.TextUtil;
import com.pug523.shelf.gui.widget.ActionButtonWidget;
import com.pug523.shelf.gui.widget.OptionWidget;

import net.minecraft.ChatFormatting;
//#if MC >= 12106
import net.minecraft.client.renderer.RenderPipelines;
//#elseif MC >= 12102
//$$ import net.minecraft.client.renderer.RenderType;
//#elseif MC >= 11500
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//#endif
import net.minecraft.resources.Identifier;
import net.minecraft.util.profiling.ProfilerFiller;

public class ConfigScreenRenderer {
    private static final Identifier RESET_BUTTON_TEXTURE = IdentifierCompat.ofShelf("textures/gui/reset_button.png");

    public void render(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, int mouseX, int mouseY, float partialTick, TabTreeController tabs, OptionContext context, OptionFocusController focus, ScrollController scrolls, ConfigChangeController change, OverlayController overlay) {
        ProfilerFiller profiler = ProfilerCompat.getProfiler();
        profiler.push("shelf_config_screen_render");

        boolean hasOverlay = overlay.hasActiveOverlay();
        int renderMouseX = hasOverlay ? -999 : mouseX;
        int renderMouseY = hasOverlay ? -999 : mouseY;

        boolean hasChanges = change.isDirty();
        screen.getFooterButtons().get(0).setEnabled(hasChanges); // undoButton
        screen.getFooterButtons().get(1).setEnabled(hasChanges); // applyButton

        LayoutConfig cfg = layout.getConfig();

        renderPanels(gui, screen, layout, cfg);
        renderHeader(gui, screen, layout, cfg);
        renderFooter(gui, screen, layout, cfg, renderMouseX, renderMouseY);
        renderTabs(gui, screen, layout, cfg, renderMouseX, renderMouseY, tabs, scrolls);
        renderOptions(gui, screen, layout, cfg, renderMouseX, renderMouseY, context, focus, scrolls);
        renderDescription(gui, screen, layout, cfg, context.items(), focus);
        renderOverlay(gui, screen, layout, cfg, mouseX, mouseY, partialTick, overlay);

        // RenderUtil.renderDebugRawQuad(gui, 150, 150, 250, 250);
        RenderUtil.renderCircle(gui, 150.0f, 150.0f, 10.0f, 0xC000FF00);

        profiler.pop();
    }

    private void renderPanels(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg) {
        gui.fill(0, 0, screen.width, screen.height, cfg.colorScreenBaseBackground);
        gui.fill(layout.tabArea.x, layout.tabArea.y, layout.tabArea.maxX, layout.tabArea.maxY, cfg.colorTabPanelBackground);
        gui.fill(layout.optionArea.x, layout.optionArea.y, layout.optionArea.maxX, layout.optionArea.maxY, cfg.colorOptionPanelBackground);
        gui.fill(layout.descArea.x, layout.descArea.y, layout.descArea.maxX, layout.descArea.maxY, cfg.colorDescriptionPanelBackground);
    }

    private void renderHeader(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg) {
        gui.fill(layout.headerArea.x, layout.headerArea.y, layout.headerArea.maxX, layout.headerArea.maxY, cfg.colorHeaderBackground);
        int textY = (layout.headerArea.height - screen.getFont().lineHeight) / 2 + 1;
        gui.text(screen.getFont(), screen.getTitle(), cfg.textPaddingX, textY, cfg.colorTextPrimary, false);
    }

    private void renderFooter(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg, int mouseX, int mouseY) {
        gui.fill(layout.footerArea.x, layout.footerArea.y, layout.footerArea.maxX, layout.footerArea.maxY, cfg.colorFooterBackground);

        List<ActionButtonWidget> footerButtons = screen.getFooterButtons();
        if (footerButtons == null) return;

        for (int i = 0; i < footerButtons.size(); i++) {
            Bounds btnBounds = layout.footerButtonBounds.get(i);
            gui.enableScissor(btnBounds.x, btnBounds.y, btnBounds.maxX, btnBounds.maxY);
            footerButtons.get(i).render(screen.getFont(), gui, layout, btnBounds.x, btnBounds.y, btnBounds.width, btnBounds.height, mouseX, mouseY);
            gui.disableScissor();
        }
    }

    private void renderTabs(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg, int mouseX, int mouseY, TabTreeController tabs, ScrollController scrolls) {
        gui.enableScissor(layout.tabArea.x, layout.tabArea.y, layout.tabArea.maxX, layout.tabArea.maxY);

        List<TabNode> flat = tabs.getFlat();
        double tabScroll = scrolls.getTabScroll();
        int textOffset = (cfg.tabItemHeight - screen.getFont().lineHeight) / 2;

        for (int i = 0; i < flat.size(); i++) {
            if (i >= layout.tabItemBounds.size())
                break;

            TabNode node = flat.get(i);
            Bounds scrolledBounds = layout.getScrolledTabBounds(i, tabScroll);

            if (scrolledBounds.y + scrolledBounds.height < layout.tabArea.y + 1 || scrolledBounds.y > screen.height) {
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
            int centerY = scrolledBounds.y + scrolledBounds.height / 2 + 1;
            if (node.hasChildren()) {
                if (node.isExpanded()) {
                    RenderUtil.renderDownwardArrow(gui, itemX + cfg.tabArrowOffsetX, centerY - 3, color);
                } else {
                    RenderUtil.renderRightwardArrow(gui, itemX + cfg.tabArrowOffsetX + 1, centerY - 4, color);
                }
            }
            gui.text(screen.getFont(), node.getName(), itemX + cfg.tabTextOffsetX, scrolledBounds.y + textOffset, color, false);
        }
        gui.disableScissor();

        renderScrollbar(gui, layout, layout.tabScrollbarTrack, tabScroll, tabs.totalHeight(cfg), cfg);
    }

    private void renderOptions(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg, int mouseX, int mouseY, OptionContext context, OptionFocusController focus, ScrollController scrolls) {
        gui.enableScissor(layout.optionArea.x, layout.optionArea.y, layout.optionArea.maxX, layout.optionArea.maxY);

        List<RenderableItem> items = context.items();
        double optionScroll = scrolls.getOptionScroll();

        for (int i = 0; i < items.size(); i++) {
            if (i >= layout.optionRows.size()) break;

            RenderableItem item = items.get(i);
            OptionRowLayout row = layout.optionRows.get(i);

            Bounds scrolledRow = layout.getScrolledOptionBounds(row.rowBounds, optionScroll);
            if (scrolledRow.y + scrolledRow.height < layout.optionArea.y + 1 || scrolledRow.y > layout.optionArea.maxY) {
                continue;
            }

            int textY = layout.getScrolledTextY(row.textY, optionScroll);

            if (item.isHeader()) {
                gui.text(screen.getFont(), item.text(), row.textX, textY, cfg.colorTextMuted, false);
                continue;
            }

            OptionWidget<?> widget = item.widget();
            Option<?> option = widget != null ? widget.getOption() : null;
            if (widget == null || option == null || row.resetButtonBounds == null) continue;

            boolean selected = focus.getFocused() == i;
            boolean hovered = mouseX >= layout.optionArea.x + 1 && mouseX < layout.optionArea.x + scrolledRow.width && mouseY >= scrolledRow.y && mouseY < scrolledRow.y + scrolledRow.height;

            if (hovered) {
                gui.fill(layout.optionArea.x, scrolledRow.y, layout.optionArea.x + scrolledRow.width, scrolledRow.y + scrolledRow.height, cfg.colorItemHoverBackground);
            }
            if (selected) {
                gui.fill(layout.optionArea.x, scrolledRow.y, layout.optionArea.x + scrolledRow.width, scrolledRow.y + scrolledRow.height, cfg.colorItemSelectedBackground);
            }

            int color = selected ? cfg.colorItemSelectedText : cfg.colorItemUnselectedText;
            gui.text(screen.getFont(), option.getName(), row.textX, textY, color, false);

            gui.enableScissor(layout.optionArea.x, layout.optionArea.y, layout.optionArea.maxX, layout.optionArea.maxY);
            widget.render(screen.getFont(), gui, layout, scrolledRow.x, scrolledRow.y, scrolledRow.width, scrolledRow.height, mouseX, mouseY);
            gui.disableScissor();

            // Reset Button Area
            Bounds scrolledReset = layout.getScrolledResetButtonBounds(row.resetButtonBounds, optionScroll);
            boolean resetHovered = scrolledReset.contains(mouseX, mouseY);
            boolean canReset = option.isPendingModifiedFromDefault();

            gui.fill(scrolledReset.x, scrolledReset.y, scrolledReset.maxX, scrolledReset.maxY, resetHovered && canReset ? 0x40FFFFFF : 0x20000000);

            int rx = scrolledReset.x + (scrolledReset.width - cfg.resetIconSize) / 2;
            int ry = scrolledReset.y + (scrolledReset.height - cfg.resetIconSize) / 2;

            renderResetIcon(gui, rx, ry, hovered, canReset, cfg);
        }
        gui.disableScissor();

        renderScrollbar(gui, layout, layout.optionScrollbarTrack, optionScroll, layout.totalOptionHeight, cfg);
    }

    private void renderResetIcon(GuiCompat gui, int rx, int ry, boolean hovered, boolean canReset, LayoutConfig cfg) {
        float alpha = canReset ? (hovered ? 1.0f : 0.8f) : 0.3f;
        float r = canReset ? 0.9f : 0.5f;
        float g = canReset ? 0.3f : 0.5f;
        float b = canReset ? 0.3f : 0.5f;

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

        Option<?> option = items.get(idx).widget().getOption();
        int wrapW = screen.width - layout.descArea.x - cfg.descTextRightPadding;

        gui.text(screen.getFont(), option.getName().copy().withStyle(ChatFormatting.BOLD), x, y, cfg.colorTextPrimary, false);
        gui.textWithWordWrap(screen.getFont(), option.getDescription(), x, y + screen.getFont().lineHeight + cfg.descTitleSpacingY, wrapW, cfg.colorTextSecondary);
    }

    private void renderOverlay(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg, int mouseX, int mouseY, float partialTick, OverlayController overlayController) {
        if (overlayController.hasActiveOverlay()) {
            ScreenOverlay overlay = overlayController.getActiveOverlay();
            if (overlay != null) {
                if (overlay.shouldDimBackground()) {
                    // TODO: move magic number to layout config
                    gui.fill(0, 0, screen.width, screen.height, 0x66000000);
                }
                overlay.render(screen.getFont(), gui, mouseX, mouseY, partialTick, layout);
            }
        }
    }

    private static int colorFromArgbFloat(float a, float r, float g, float b) {
        return (int) (a * 255.0f) << 24 | (int) (r * 255.0f) << 16 | (int) (g * 255.0f) << 8 | (int) (b * 255.0f);
    }
}
