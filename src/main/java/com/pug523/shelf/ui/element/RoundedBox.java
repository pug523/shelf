package com.pug523.shelf.ui.element;

import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.core.Colors;
import com.pug523.shelf.ui.Element;
import com.pug523.shelf.ui.render.RenderUtil;
import com.pug523.shelf.core.geometry.MousePos;
import com.pug523.shelf.core.geometry.Rect;
import net.minecraft.client.gui.Font;

public class RoundedBox implements Element {
    private final Config config;

    public static class Config {
        public float cornerRadius = 3.0f;
        public int color = Colors.ALPHA_BLACK_53;
    }

    public RoundedBox(Config config) {
        this.config = config;
    }

    public Config config() {
        return config;
    }

    @Override
    public void render(Font font, GuiCompat gui, Rect rect, MousePos mousePos) {
        RenderUtil.renderRoundedRect(gui, rect.x, rect.y, rect.width, rect.height, config.cornerRadius, config.color);
    }
}
