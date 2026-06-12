package com.pug523.shelf.gui.renderer;

import java.util.List;

import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.compat.IdentifierCompat;
import com.pug523.shelf.config.Option;
import com.pug523.shelf.gui.ConfigScreen;
import com.pug523.shelf.gui.TabNode;
import com.pug523.shelf.gui.controller.OptionFocusController;
import com.pug523.shelf.gui.controller.ScrollController;
import com.pug523.shelf.gui.controller.TabTreeController;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.model.OptionContext;
import com.pug523.shelf.gui.model.RenderableItem;
import com.pug523.shelf.gui.text.TextUtil;
import com.pug523.shelf.gui.widget.ClickableWidget;
import com.pug523.shelf.gui.widget.OptionWidget;

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
import net.minecraft.util.Mth;

public class ConfigScreenRenderer {
    private static final Identifier RESET_BUTTON_TEXTURE = IdentifierCompat.of("shelf",
            "textures/gui/reset_button.png");

    public void render(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, int mouseX, int mouseY,
            TabTreeController tabs, OptionContext context, OptionFocusController focus, ScrollController scrolls) {
        LayoutConfig cfg = layout.getConfig();

        SdfRenderQueue.startBuffering();

        base(gui, screen, layout, cfg);

        header(gui, screen, layout, cfg);
        footer(gui, screen, layout, cfg, screen.getFooterButtons(), mouseX, mouseY);

        middle(gui, screen, layout, cfg);

        tabs(gui, screen, layout, cfg, tabs, scrolls);

        options(gui, screen, layout, cfg, context.items(), focus, scrolls, mouseX, mouseY);

        description(gui, screen, layout, cfg, context.items(), focus);

        //#if MC <= 12103
        //$$ SdfRenderQueue.flushAll();
        //#endif
    }

    private void base(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg) {
        gui.fill(0, 0, screen.width, screen.height, cfg.colorScreenBaseBackground);
    }

    private void header(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg) {
        gui.fill(0, 0, screen.width, cfg.topBarHeight, cfg.colorHeaderBackground);
        gui.text(screen.getFont(), screen.getTitle(), cfg.textPaddingX,
                (cfg.topBarHeight - screen.getFont().lineHeight) / 2 + 1, cfg.colorTextPrimary, false);
    }

    private void footer(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg,
            List<ClickableWidget> footerButtons, int mouseX, int mouseY) {
        gui.fill(0, screen.height - cfg.bottomBarHeight, screen.width, screen.height, cfg.colorFooterBackground);

        if (footerButtons == null || footerButtons.isEmpty())
            return;

        int buttonWidth = 60;
        int buttonHeight = 20;
        int padding = 10;
        int spacing = 5;

        // Line up footer buttons from the right.
        int currentX = screen.width - buttonWidth - padding;
        int buttonY = screen.height - cfg.bottomBarHeight + (cfg.bottomBarHeight - buttonHeight) / 2;

        for (int i = footerButtons.size() - 1; i >= 0; i--) {
            ClickableWidget button = footerButtons.get(i);
            button.render(screen.getFont(), gui, layout, currentX, buttonY, buttonWidth, buttonHeight, mouseX, mouseY);
            currentX -= (buttonWidth + spacing);
        }
    }

    private void middle(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg) {
        gui.fill(0, cfg.topBarHeight, layout.tabAreaWidth, screen.height - cfg.bottomBarHeight,
                cfg.colorTabPanelBackground);

        gui.fill(layout.tabAreaWidth, cfg.topBarHeight, layout.descAreaX, screen.height - cfg.bottomBarHeight,
                cfg.colorOptionPanelBackground);

        gui.fill(layout.descAreaX, cfg.topBarHeight, screen.width, screen.height - cfg.bottomBarHeight,
                cfg.colorDescriptionPanelBackground);
    }

    private void tabs(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg, TabTreeController tabs,
            ScrollController scrolls) {
        List<TabNode> flat = tabs.getFlat();

        gui.enableScissor(0, cfg.topBarHeight + 1, layout.tabAreaWidth, screen.height - cfg.bottomBarHeight);

        int tabHeight = cfg.tabItemHeight;
        int contentHeight = flat.size() * tabHeight;
        double scroll = scrolls.getTabScroll();
        int textOffset = (tabHeight - screen.getFont().lineHeight) / 2;

        for (int i = 0; i < flat.size(); i++) {
            TabNode node = flat.get(i);
            int x = cfg.textPaddingX + (node.getDepth() * cfg.tabTreeIndentation);
            int y = cfg.topBarHeight + cfg.tabItemStartOffsetY + (i * tabHeight) - (int) scroll;
            int centerY = y + tabHeight / 2 + 1;

            int color = (node == tabs.getSelected()) ? cfg.colorItemSelectedText : cfg.colorItemUnselectedText;

            if (node == tabs.getSelected()) {
                gui.fill(0, y, layout.tabAreaWidth, y + tabHeight, cfg.colorItemSelectedBackground);
            }

            if (node.hasChildren()) {
                if (node.isExpanded()) {
                    RenderUtil.renderDownwardArrow(gui, x + 2, centerY - 3, color);
                } else {
                    RenderUtil.renderRightwardArrow(gui, x + 3, centerY - 4, color);
                }
            }

            x += 12;
            gui.text(screen.getFont(), node.getName(), x, y + textOffset, color, false);
        }

        gui.disableScissor();

        drawScrollBar(gui, layout.tabAreaWidth - cfg.scrollbarWidth, cfg.topBarHeight + 1, layout.mainContentHeight - 1,
                scroll, contentHeight + cfg.tabItemStartOffsetY, cfg);
    }

    private void options(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg,
            List<RenderableItem> items, OptionFocusController focus, ScrollController scrolls, int mouseX, int mouseY) {
        gui.enableScissor(layout.tabAreaWidth + 1, cfg.topBarHeight + 1, layout.descAreaX,
                screen.height - cfg.bottomBarHeight);

        int optionHeight = cfg.optionItemHeight;

        int extraPadding = 0;
        int headerOffset = 12;

        int contentHeight = calculateOptionHeight(items, optionHeight, headerOffset);
        double scroll = scrolls.getOptionScroll();

        for (int i = 0; i < items.size(); i++) {
            RenderableItem item = items.get(i);
            if (item.isHeader() && i > 0) {
                extraPadding += headerOffset;
            }

            int y = cfg.topBarHeight + cfg.optionItemStartOffsetY + (i * optionHeight) + extraPadding - (int) scroll;

            if (item.isHeader()) {
                gui.text(screen.getFont(), item.text(), layout.tabAreaWidth + cfg.optionHeaderOffsetX,
                        y + (optionHeight - screen.getFont().lineHeight) / 2, cfg.colorTextMuted, false);
                continue;
            }

            OptionWidget<?> widget = item.widget();
            Option<?> option = widget != null ? widget.getOption() : null;
            if (widget == null || option == null)
                continue;

            boolean selected = focus.getFocused() == i;
            boolean hovered = mouseX >= layout.tabAreaWidth && mouseX < layout.descAreaX && mouseY >= y
                    && mouseY < y + optionHeight;

            if (hovered) {
                gui.fill(layout.tabAreaWidth + 1, y, layout.descAreaX, y + optionHeight, cfg.colorItemHoverBackground);
            }
            if (selected) {
                gui.fill(layout.tabAreaWidth + 1, y, layout.descAreaX, y + optionHeight,
                        cfg.colorItemSelectedBackground);
            }

            int color = selected ? cfg.colorItemSelectedText : cfg.colorItemUnselectedText;

            int textX = layout.tabAreaWidth + cfg.optionTextOffsetX;
            int textY = y + (optionHeight - screen.getFont().lineHeight) / 2 + 1;
            gui.text(screen.getFont(), option.getName(), textX, textY, color, false);

            widget.render(screen.getFont(), gui, layout, layout.tabAreaWidth, y,
                    layout.optionAreaWidth - cfg.resetButtonWidth - 12, optionHeight, mouseX, mouseY);

            drawResetButton(gui, screen, layout, cfg, option, mouseX, mouseY, y);
        }

        gui.disableScissor();

        drawScrollBar(gui, layout.descAreaX - cfg.scrollbarWidth, cfg.topBarHeight + 1, layout.mainContentHeight - 1,
                scroll, contentHeight + cfg.optionItemStartOffsetY, cfg);
    }

    private int calculateOptionHeight(List<RenderableItem> items, int optionHeight, int headerGap) {
        int extra = 0;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isHeader() && i > 0) {
                extra += headerGap;
            }
        }
        return items.size() * optionHeight + extra;
    }

    private void drawResetButton(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg,
            Option<?> option, int mouseX, int mouseY, int y) {
        int x = layout.descAreaX - cfg.resetButtonWidth - 6;
        int h = 16;
        int iconWidth = 14;
        int iconHeight = 14;

        boolean hovered = mouseX >= x && mouseX < x + cfg.resetButtonWidth && mouseY >= y + 4 && mouseY < y + 4 + h;
        boolean canReset = option.isPendingModifiedFromDefault();

        gui.fill(x, y + 4, x + cfg.resetButtonWidth, y + 4 + h, hovered && canReset ? 0x40FFFFFF : 0x20000000);

        int tx = x + (cfg.resetButtonWidth - iconWidth) / 2;
        int ty = y + 4 + (h - iconHeight) / 2;

        float alpha;
        float r;
        float g;
        float b;
        if (canReset) {
            alpha = hovered ? 1.0f : 0.8f;
            r = 0.9f;
            g = 0.3f;
            b = 0.3f;
        } else {
            alpha = 0.3f;
            r = 0.5f;
            g = 0.5f;
            b = 0.5f;
        }

        //#if MC >= 12106
        gui.blit(RenderPipelines.GUI_TEXTURED, RESET_BUTTON_TEXTURE, tx, ty, 0.0f, 0.0f, iconWidth, iconHeight,
                iconWidth, iconHeight, colorFromArgbFloat(alpha, r, g, b));
        //#elseif MC >= 12102
        //$$ gui.blit(RenderType::guiTextured, RESET_BUTTON_TEXTURE, tx, ty, 0.0f, 0.0f, iconWidth, iconHeight,
        //$$         iconWidth, iconHeight, colorFromArgbFloat(alpha, r, g, b));
        //#else
        //$$ gui.blit(RESET_BUTTON_TEXTURE, tx, ty, 0.0f, 0.0f, iconWidth, iconHeight,
        //$$         iconWidth, iconHeight, r, g, b, alpha);
        //#endif
    }

    private static int colorFromArgbFloat(float a, float r, float g, float b) {
        return (Mth.floor(a * 255.0f) & 0xFF) << 24 | (Mth.floor(r * 255.0f) & 0xFF) << 16
                | (Mth.floor(g * 255.0f) & 0xFF) << 8 | Mth.floor(b * 255.0f) & 0xFF;
    }

    private void description(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg,
            List<RenderableItem> items, OptionFocusController focus) {
        int idx = focus.getFocused();
        if (idx < 0 || idx >= items.size()) {
            drawEmpty(gui, screen, layout, cfg);
            return;
        }

        RenderableItem item = items.get(idx);
        if (item.isHeader() || item.widget() == null) {
            drawEmpty(gui, screen, layout, cfg);
            return;
        }

        Component name = item.widget().getOption().getName();
        Component desc = item.widget().getOption().getDescription();

        int x = layout.descAreaX + cfg.descTextOffsetX;
        int y = cfg.topBarHeight + cfg.descTextOffsetY;
        int w = screen.width - layout.descAreaX - cfg.descTextRightPadding;

        gui.text(screen.getFont(), name.copy().withStyle(ChatFormatting.BOLD), x, y, cfg.colorTextPrimary, false);
        gui.textWithWordWrap(screen.getFont(), desc, x, y + screen.getFont().lineHeight + 12, w,
                cfg.colorTextSecondary);
    }

    private void drawEmpty(GuiCompat gui, ConfigScreen screen, LayoutEngine layout, LayoutConfig cfg) {
        gui.text(screen.getFont(), TextUtil.guiText("select_an_option"), layout.descAreaX + cfg.descTextOffsetX,
                cfg.topBarHeight + cfg.descTextOffsetY, cfg.colorTextDisabled, false);
    }

    private void drawScrollBar(GuiCompat gui, int x, int y, int height, double scroll, int contentHeight,
            LayoutConfig cfg) {
        if (contentHeight <= height)
            return;

        int barHeight = Math.max(cfg.scrollbarMinHeight, (int) ((height / (float) contentHeight) * height));
        int maxScroll = contentHeight - height;
        int barY = y + (int) ((scroll / maxScroll) * (height - barHeight));

        gui.fill(x, y, x + cfg.scrollbarWidth, y + height, cfg.colorScrollBarTrack);
        gui.fill(x, barY, x + cfg.scrollbarWidth, barY + barHeight, cfg.colorScrollBarThumb);
    }
}
