package com.pug523.shelf.ui.element;

import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.gui.Colors;
import com.pug523.shelf.ui.Element;
import com.pug523.shelf.ui.render.RenderUtil;
import com.pug523.shelf.core.geometry.MousePos;
import com.pug523.shelf.core.geometry.Rect;
import net.minecraft.client.gui.Font;

public class RectangleBox implements Element {
    private final Config config;

    public static class Config {
        public int borderThickness = 1;
        public int innerColor = Colors.ALPHA_BLACK_53;
        public int outlineColor = Colors.BORDER_BLACK;
    }

    public RectangleBox(Config config) {
        this.config = config;
    }

    public Config config() {
        return config;
    }

    @Override
    public void render(Font font, GuiCompat gui, Rect rect, MousePos mousePos) {
        RenderUtil.renderOutline(gui, rect.x, rect.y, rect.width, rect.height, config.borderThickness,
                config.outlineColor);
        RenderUtil.renderInner(gui, rect.x, rect.y, rect.width, rect.height, config.borderThickness, config.innerColor);
    }
}
