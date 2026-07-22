package com.pug523.shelf.ui.component;

import java.util.function.Function;

import com.pug523.shelf.common.compat.ComponentCompat;
import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.core.geometry.MousePos;
import com.pug523.shelf.core.geometry.Rect;
import com.pug523.shelf.ui.Widget;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class SelectorItemWidget<E> implements Widget {
    private final E value;
    private final Function<E, Component> labelFormatter;
    private final boolean selected;
    private final boolean multiSelect;

    private final Config config;

    public static class Config {
        // TODO: change this
        public int textDefaultColor = 0xFFFFFFFF;
        public int textHoverColor = 0xFFFFFFFF;
        public int textSingleSelectedColor = 0xFFFFFFFF;
        public int hoverBackgroundColor = 0xFFFFFFFF;
        public int defaultBackgroundColor = 0xFFFFFFFF;
        public int selectedCheckmarkColor = 0xFFFFFFFF;
        public int iconSpacing = 4;
    }

    public SelectorItemWidget(
        E value,
        Function<E, Component> labelFormatter,
        boolean selected,
        boolean isMultiSelect,
        Config config

    ) {
        this.value = value;
        this.labelFormatter = labelFormatter;
        this.selected = selected;
        this.multiSelect = isMultiSelect;
        this.config = config;
    }

    public E getValue() {
        return this.value;
    }

    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public void render(Font font, GuiCompat gui, Rect rect, MousePos mousePos) {
        boolean isHovered = mousePos.isHovering(rect);

        int bgColor = isHovered ? config.hoverBackgroundColor : config.defaultBackgroundColor;
        gui.fill(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, bgColor);

        Component text = this.labelFormatter.apply(this.value);
        int textX = rect.x + config.iconSpacing + 4;
        int textY = rect.y + (rect.height - font.lineHeight) / 2 + 1;
        int textColor = isHovered ? config.textHoverColor : config.textDefaultColor;

        if (multiSelect) {
            String prefix = this.selected ? "[x] " : "[ ] ";
            Component combinedText = ComponentCompat.literal(prefix).append(text);
            gui.text(font, combinedText, textX, textY, textColor, false);
        } else {
            int finalColor = this.selected ? config.textSingleSelectedColor : textColor;
            gui.text(font, text, textX, textY, finalColor, false);

            if (this.selected) {
                // TODO: replace this with texture or something
                Component check = ComponentCompat.literal("✓");
                int checkWidth = ComponentCompat.width(font, check);
                gui.text(font, check, rect.x + rect.width - checkWidth - 6, textY, config.selectedCheckmarkColor, false);
            }
        }
    }

    @Override
    public boolean mouseClicked(Rect rect, MousePos mousePos, int button, int modifiers) {
        return true;
    }

}
