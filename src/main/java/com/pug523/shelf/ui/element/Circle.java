package com.pug523.shelf.ui.element;

import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.core.Colors;
import com.pug523.shelf.ui.Element;
import com.pug523.shelf.ui.render.RenderUtil;
import com.pug523.shelf.core.geometry.MousePos;
import com.pug523.shelf.core.geometry.Rect;
import net.minecraft.client.gui.Font;

public class Circle implements Element {
    private final Config config;

    public static class Config {
        public int color = Colors.ALPHA_BLACK_53;
    }

    public Circle(Config config) {
        this.config = config;
    }

    public Config config() {
        return config;
    }

    @Override
    public void render(Font font, GuiCompat gui, Rect rect, MousePos mousePos) {
        int centerX = rect.x + rect.width / 2;
        int centerY = rect.y + rect.height / 2 + 1;
        int radius = Math.min(rect.width / 2, rect.height / 2);
        RenderUtil.renderCircle(gui, centerX, centerY, radius, config.color);
    }
}
