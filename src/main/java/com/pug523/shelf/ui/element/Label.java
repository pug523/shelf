package com.pug523.shelf.ui.element;

import com.pug523.shelf.common.compat.ComponentCompat;
import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.core.Colors;
import com.pug523.shelf.core.geometry.MousePos;
import com.pug523.shelf.core.geometry.Rect;
import com.pug523.shelf.ui.Element;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class Label implements Element {
    private Component text;
    private final Config config;

    public enum Alignment {
        LEFT_TOP, LEFT_MIDDLE, LEFT_BOTTOM, CENTER_TOP, CENTER_MIDDLE, CENTER_BOTTOM, RIGHT_TOP, RIGHT_MIDDLE,
        RIGHT_BOTTOM,
    }

    public static class Config {
        public long scrollStartWait = 1000L;
        public long scrollSpeed = 24L;
        public long scrollEndWait = 1500L;
        public int color = Colors.WHITE;
        public boolean shadow = true;
        public Alignment alignment = Alignment.CENTER_MIDDLE;
    }

    private long startTime = -1L;

    public Label(Component label, Config config) {
        this.text = label;
        this.config = config;
    }

    public void text(Component text) {
        this.text = text;
    }

    public Config config() {
        return config;
    }

    public void resetScroll() {
        startTime = -1L;
    }

    public boolean isScrolling() {
        return startTime != -1L;
    }

    public void startScrolling() {
        startTime = System.currentTimeMillis();
    }

    private int renderX(Rect rect, int textWidth) {
        switch (config.alignment) {
        case LEFT_TOP:
        case LEFT_MIDDLE:
        case LEFT_BOTTOM:
            return rect.x;
        case CENTER_TOP:
        case CENTER_MIDDLE:
        case CENTER_BOTTOM:
            return rect.x + (rect.width - textWidth) / 2 + 1;
        case RIGHT_TOP:
        case RIGHT_MIDDLE:
        case RIGHT_BOTTOM:
            return rect.maxX() - textWidth;
        }
        return rect.x;
    }

    private int renderY(Rect rect, int lineHeight) {
        switch (config.alignment) {
        case LEFT_TOP:
        case CENTER_TOP:
        case RIGHT_TOP:
            return rect.y;
        case LEFT_MIDDLE:
        case CENTER_MIDDLE:
        case RIGHT_MIDDLE:
            return rect.y + (rect.height - lineHeight) / 2 + 1;
        case LEFT_BOTTOM:
        case CENTER_BOTTOM:
        case RIGHT_BOTTOM:
            return rect.maxY() - lineHeight;
        }
        return rect.y;
    }

    @Override
    public void render(Font font, GuiCompat gui, Rect rect, MousePos mousePos) {
        gui.enableScissor(rect.x, rect.y, rect.maxX(), rect.maxY());

        int textWidth = ComponentCompat.width(font, text);
        int overflow = textWidth - rect.width;

        if (overflow > 0) {
            if (!isScrolling()) {
                startScrolling();
            }

            long scrollTime = overflow * config.scrollSpeed;
            long totalCycleTime = config.scrollStartWait + scrollTime + config.scrollEndWait;
            long currentDuration = isScrolling() ? System.currentTimeMillis() - startTime : 0L;
            long cycleTime = currentDuration % totalCycleTime;

            double progress = 0.0;
            if (cycleTime > config.scrollStartWait && cycleTime <= config.scrollStartWait + scrollTime) {
                progress = (double) (cycleTime - config.scrollStartWait) / (double) scrollTime;
            } else if (cycleTime > config.scrollStartWait + scrollTime) {
                progress = 1.0;
            }

            // When scrolling, we override horizontal alignment to left-align
            // and subtract the scroll offset.
            int x = rect.x - (int) (progress * overflow);
            int y = renderY(rect, font.lineHeight);
            gui.text(font, text, x, y, config.color, config.shadow);
        } else {
            resetScroll();
            gui.text(font, text, renderX(rect, textWidth), renderY(rect, font.lineHeight), config.color, config.shadow);
        }
        gui.disableScissor();
    }
}
