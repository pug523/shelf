package com.pug523.shelf.ui.option;

import com.pug523.shelf.common.compat.ComponentCompat;
import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.ui.layout.LayoutConfig;
import com.pug523.shelf.ui.layout.LayoutEngine;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.mojang.datafixers.util.Pair;

// TODO: refactor
public class CyclingOptionWidget<T> extends OptionWidget<T> {
    private final List<Pair<Component, T>> labelAndValues;
    private final ActionButtonWidget buttonDelegate;

    private int currentIdx = 0;
    private int longestWidthWithPadding = 0;

    public CyclingOptionWidget(GuiOption<T> option, List<Pair<Component, T>> labelAndValues) {
        super(option);
        addWarningIfListIsEmpty(labelAndValues);
        this.labelAndValues = labelAndValues;
        this.buttonDelegate = new ActionButtonWidget(labelAndValues.get(currentIdx).getFirst(), btn -> this.cycleOne());
        updateCurrentIdxByOptionValue();
    }

    public CyclingOptionWidget(GuiOption<T> option) {
        this(option, new ArrayList<>());
    }

    private static <T> void addWarningIfListIsEmpty(List<Pair<Component, T>> list) {
        if (list.isEmpty()) {
            list.add(new Pair<>(ComponentCompat.literal("Empty").withStyle(ChatFormatting.YELLOW), null));
        }
    }

    private void updateCurrentIdxByOptionValue() {
        T currentValue = getPendingValue();
        for (int i = 0; i < labelAndValues.size(); i++) {
            Pair<Component, T> pair = labelAndValues.get(i);
            if (pair.getSecond() == currentValue) {
                setTo(i);
                break;
            }
        }
    }

    public static <E extends Enum<E>> CyclingOptionWidget<E> of(GuiOption<E> option, Class<E> enumClass, Function<E, Component> labelFactory) {
        List<Pair<Component, E>> list = new ArrayList<>();
        for (E enumValue : enumClass.getEnumConstants()) {
            list.add(Pair.of(labelFactory.apply(enumValue), enumValue));
        }
        return new CyclingOptionWidget<>(option, list);
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX, int mouseY) {
        LayoutConfig cfg = layout.getConfig();

        if (longestWidthWithPadding == 0) {
            for (Pair<Component, T> pair : labelAndValues) {
                longestWidthWithPadding = ComponentCompat.width(font, pair.getFirst()) + cfg.cyclingButtonWidthPadding;
            }
        }
        int btnX = x + width - layout.optionWidgetRightMargin - this.longestWidthWithPadding;
        int btnY = y + (height - cfg.cyclingButtonHeight) / 2;
        this.buttonDelegate.render(font, gui, layout, btnX, btnY, this.longestWidthWithPadding, cfg.cyclingButtonHeight, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        return this.buttonDelegate.mouseClicked(mouseX, mouseY, button, modifiers, layout);
    }

    private void cycleOne() {
        setTo((currentIdx + 1) % labelAndValues.size());
    }

    private void setTo(int idx) {
        currentIdx = idx;
        Pair<Component, T> labelAndValue = labelAndValues.get(currentIdx);
        buttonDelegate.setLabel(labelAndValue.getFirst());
        setPendingValue(labelAndValue.getSecond());
    }
}
