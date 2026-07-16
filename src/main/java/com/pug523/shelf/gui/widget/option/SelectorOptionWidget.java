package com.pug523.shelf.gui.widget.option;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.pug523.shelf.compat.ComponentCompat;
import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.compat.ScreenCompat;
import com.pug523.shelf.gui.ConfigScreen;
import com.pug523.shelf.gui.controller.OverlayController;
import com.pug523.shelf.gui.layout.Bounds;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;
import com.pug523.shelf.gui.renderer.RenderUtil;
import com.pug523.shelf.gui.widget.SelectorItemWidget;
import com.pug523.shelf.gui.widget.overlay.DropdownOverlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

//#if MC >= 12111
import com.mojang.blaze3d.platform.cursor.CursorTypes;
//#endif

public class SelectorOptionWidget<T, E> extends OptionWidget<T> {
    private final List<E> candidates;
    private final Function<E, Component> labelExtractor;
    private final boolean multiSelect;
    private final Function<T, List<E>> toListConverter;
    private final Function<List<E>, T> fromListConverter;

    private Bounds cachedBounds;

    public SelectorOptionWidget(
        GuiOption<T> option,
        List<E> candidates,
        Function<E, Component> labelExtractor,
        boolean multiSelect,
        Function<T, List<E>> toListConverter,
        Function<List<E>, T> fromListConverter
    ) {
        super(option);
        this.candidates = candidates;
        this.labelExtractor = labelExtractor;
        this.multiSelect = multiSelect;
        this.toListConverter = toListConverter;
        this.fromListConverter = fromListConverter;
    }

    public static <E> SelectorOptionWidget<E, E> single(GuiOption<E> option, List<E> candidates, Function<E, Component> labelExtractor) {
        return new SelectorOptionWidget<>(
            option,
            candidates,
            labelExtractor,
            false,
            val -> {
                List<E> list = new ArrayList<>();
                if (val != null) {
                    list.add(val);
                }
                return list;
            },
            list -> list.isEmpty() ? null : list.get(0)
        );
    }

    public static <E> SelectorOptionWidget<List<E>, E> multi(GuiOption<List<E>> option, List<E> candidates, Function<E, Component> labelExtractor) {
        return new SelectorOptionWidget<>(
            option,
            candidates,
            labelExtractor,
            true,
            val -> val != null ? val : new ArrayList<>(),
            list -> list
        );
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX, int mouseY) {
        LayoutConfig cfg = layout.getConfig();

        int selectorWidth = 150;
        int selectorX = x + width - layout.optionWidgetRightMargin - selectorWidth;
        int selectorY = y + (height - (font.lineHeight + 8)) / 2;
        int selectorHeight = font.lineHeight + 8;

        this.cachedBounds = new Bounds(selectorX, selectorY, selectorWidth, selectorHeight);

        boolean isHovered = cachedBounds.contains(mouseX, mouseY);
        int borderColor = isHovered ? cfg.colorColorPickerBorderHover : cfg.colorColorPickerBorderDefault;
        int bgColor = cfg.colorItemInputSuggestionBg;

        //#if MC >= 12111
        if (isHovered) {
            gui.requestCursor(CursorTypes.POINTING_HAND);
        }
        //#endif

        gui.fill(selectorX, selectorY, selectorX + selectorWidth, selectorY + selectorHeight, borderColor);
        gui.fill(selectorX + 1, selectorY + 1, selectorX + selectorWidth - 1, selectorY + selectorHeight - 1, bgColor);

        List<E> selected = toListConverter.apply(this.getPendingValue());
        Component displayText;
        if (selected.isEmpty()) {
            displayText = ComponentCompat.literal("None");
        } else if (selected.size() == 1) {
            displayText = labelExtractor.apply(selected.get(0));
        } else {
            if (multiSelect) {
                displayText = ComponentCompat.literal(selected.size() + " selected");
            } else {
                displayText = labelExtractor.apply(selected.get(0));
            }
        }

        int textY = selectorY + (selectorHeight - font.lineHeight) / 2 + 1;
        int maxTextWidth = selectorWidth - 25;

        String rawString = font.plainSubstrByWidth(displayText.getString(), maxTextWidth);
        Component finalDrawText = ComponentCompat.literal(rawString);

        gui.text(font, finalDrawText, selectorX + 8, textY, cfg.colorTextSecondary);

        RenderUtil.renderDownwardArrow(gui, selectorX + selectorWidth - 8, textY, cfg.colorTextSecondary);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        if (this.cachedBounds != null && this.cachedBounds.contains(mouseX, mouseY)) {
            Screen screen = ScreenCompat.getScreen(Minecraft.getInstance());
            if (screen instanceof ConfigScreen) {
                triggerDropdown((ConfigScreen) screen, layout);
                return true;
            }
        }
        return false;
    }

    private void triggerDropdown(ConfigScreen screen, LayoutEngine layout) {
        OverlayController overlayController = screen.getOverlayController();
        overlayController.clear();

        LayoutConfig cfg = layout.getConfig();
        int boxX = this.cachedBounds.x;
        int boxY = this.cachedBounds.y + this.cachedBounds.height + 2;
        int boxWidth = this.cachedBounds.width;
        int rowHeight = cfg.itemInputRowHeight > 0 ? cfg.itemInputRowHeight : 20;

        List<E> currentSelected = toListConverter.apply(this.getPendingValue());

        List<SelectorItemWidget<E>> items = candidates.stream().map(candidate -> {
            boolean isSelected = currentSelected.contains(candidate);
            return new SelectorItemWidget<>(candidate, labelExtractor, isSelected, multiSelect);
        }).collect(Collectors.toList());

        DropdownOverlay dropdown = new DropdownOverlay(
            boxX,
            boxY,
            boxWidth,
            rowHeight,
            items,
            overlayController::pop,
            clickedWidget -> {
                @SuppressWarnings("unchecked")
                SelectorItemWidget<E> clickedItem = (SelectorItemWidget<E>) clickedWidget;
                E value = clickedItem.getValue();

                List<E> nextSelected = new ArrayList<>(toListConverter.apply(this.getPendingValue()));

                if (multiSelect) {
                    if (nextSelected.contains(value)) {
                        nextSelected.remove(value);
                    } else {
                        nextSelected.add(value);
                    }
                    this.setPendingValue(fromListConverter.apply(nextSelected));

                    triggerDropdown(screen, layout);
                } else {
                    nextSelected.clear();
                    nextSelected.add(value);
                    this.setPendingValue(fromListConverter.apply(nextSelected));

                    overlayController.pop();
                }
            }
        );

        overlayController.push(dropdown);
    }
}
