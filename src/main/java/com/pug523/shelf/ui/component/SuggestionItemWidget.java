package com.pug523.shelf.ui.component;

import java.util.List;
import java.util.function.Function;

import com.pug523.shelf.common.compat.ComponentCompat;
import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.core.Colors;
import com.pug523.shelf.core.geometry.MousePos;
import com.pug523.shelf.core.geometry.Rect;
import com.pug523.shelf.ui.Widget;
import com.pug523.shelf.ui.element.RectangleBox;
import com.pug523.shelf.ui.element.Label;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

// TODO: refactor
public class SuggestionItemWidget<T> implements Widget {
    private final Label label;
    private final RectangleBox backgroundBox;
    private final T value;
    private final Function<T, String> idExtractor;
    private final TextInputFieldWidget.SuggestionIconRenderer<T> iconRenderer;
    private final List<Runnable> listeners;

    private final Config config;

    private boolean cachedHovered = false;

    public static class Config {
        public int iconSpacing = 2;
        public int iconSize = 14;
        public int textColor = Colors.ALPHA_WHITE_60;
        public int textHoverColor = Colors.WHITE;
        public int backgroundColor = Colors.ALPHA_BLACK_53;
        public int hoverBackgroundColor = Colors.INDIGO;
    }

    public SuggestionItemWidget(Label label, RectangleBox backgroundBox, T value, Function<T, String> idExtractor,
                                TextInputFieldWidget.SuggestionIconRenderer<T> iconRenderer, List<Runnable> listeners, Config config) {
        this.label = label;
        this.backgroundBox = backgroundBox;
        this.value = value;
        this.idExtractor = idExtractor;
        this.iconRenderer = iconRenderer;
        this.listeners = listeners;
        this.config = config;

        setupElements();
    }

    public Config config() {
        return config;
    }

    public T value() {
        return value;
    }

    private void setupElements() {
    }

    private void rebuildElements(Rect rect) {
        backgroundBox.config().borderThickness = 0;
        int bgColor = cachedHovered ? config.hoverBackgroundColor : config.backgroundColor;
        backgroundBox.config().innerColor = bgColor;

        String idStr = idExtractor.apply(value);
        Component text = ComponentCompat.literal(idStr);
        label.text(text);
        int textColor = cachedHovered ? config.textHoverColor : config.textColor;
        label.config().color = textColor;
    }

    @Override
    public void render(Font font, GuiCompat gui, Rect rect, MousePos mousePos) {
        boolean hovered = mousePos.isHovering(rect);
        if (hovered != cachedHovered) {
            cachedHovered = hovered;
            rebuildElements(rect);
        }
        backgroundBox.render(font, gui, rect, mousePos);

        if (iconRenderer != null) {
            int iconX = rect.x + config.iconSpacing;
            Rect iconRect = new Rect(iconX, rect.y, config.iconSize, config.iconSize);
            iconRenderer.render(gui, value, iconRect);

            int labelX = iconX + config.iconSize + config.iconSpacing;
            int labelWidth = rect.width - labelX;
            Rect labelRect = new Rect(labelX, rect.y, labelWidth, rect.height);
            label.render(font, gui, labelRect, mousePos);
        } else {
            label.render(font, gui, rect, mousePos);
        }
    }

    @Override
    public boolean mouseClicked(Rect rect, MousePos mousePos, int button, int modifiers) {
        if (mousePos.isHovering(rect)) {
            this.listeners.forEach(Runnable::run);
            return true;
        }
        return false;
    }
}
