package com.pug523.shelf.gui.widget;

import java.util.function.Function;

import com.pug523.shelf.compat.ComponentCompat;
import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.layout.LayoutEngine;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class SelectorItemWidget<E> implements ClickableWidget {
    private final E value;
    private final Function<E, Component> labelFormatter;
    private final boolean selected;
    private final boolean isMultiSelect;

    public SelectorItemWidget(
        E value,
        Function<E, Component> labelFormatter,
        boolean selected,
        boolean isMultiSelect
    ) {
        this.value = value;
        this.labelFormatter = labelFormatter;
        this.selected = selected;
        this.isMultiSelect = isMultiSelect;
    }

    public E getValue() {
        return this.value;
    }

    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public void render(Font font, GuiCompat gui, LayoutEngine layout, int x, int y, int width, int height, int mouseX, int mouseY) {
        LayoutConfig cfg = layout.getConfig();
        boolean isHovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;

        int bgColor = isHovered ? cfg.colorItemInputSuggestionHover : cfg.colorItemInputSuggestionBg;
        gui.fill(x, y, x + width, y + height, bgColor);

        Component text = this.labelFormatter.apply(this.value);
        int textX = x + cfg.itemInputIconSpacing + 4;
        int textY = y + (height - font.lineHeight) / 2 + 1;
        int textColor = isHovered ? cfg.colorItemInputSuggestionTextHover : cfg.colorItemInputSuggestionTextDefault;

        if (isMultiSelect) {
            String prefix = this.selected ? "[x] " : "[ ] ";
            Component combinedText = ComponentCompat.literal(prefix).append(text);
            gui.text(font, combinedText, textX, textY, textColor, false);
        } else {
            int finalColor = this.selected ? cfg.colorTextSecondary : textColor;
            gui.text(font, text, textX, textY, finalColor, false);

            if (this.selected) {
                // TODO: replace this with texture or something
                Component check = ComponentCompat.literal("✓");
                int checkWidth = ComponentCompat.width(font, check);
                gui.text(font, check, x + width - checkWidth - 6, textY, cfg.colorTextSecondary, false);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button, int modifiers, LayoutEngine layout) {
        return true;
    }
}
