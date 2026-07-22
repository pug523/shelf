package com.pug523.shelf.ui.element;

import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.gui.Colors;
import com.pug523.shelf.ui.Element;
import com.pug523.shelf.ui.render.RenderUtil;
import com.pug523.shelf.core.geometry.MousePos;
import com.pug523.shelf.core.geometry.Rect;
import net.minecraft.client.gui.Font;

public class Capsule implements Element {
    private final Config config;

    public static class Config {
        public int startColor = Colors.ALPHA_BLACK_53;
        public int endColor = Colors.ALPHA_BLACK_53;
        public Orientation orientation;

        public enum Orientation {
            HORIZONTAL,
            VERTICAL,
        }
    }

    public Capsule(Config config) {
        this.config = config;
    }

    public Config config() {
        return config;
    }

    @Override
    public void render(Font font, GuiCompat gui, Rect rect, MousePos mousePos) {
        if (config.orientation == Config.Orientation.HORIZONTAL) {
            RenderUtil.renderCapsuleHorizontal(gui, rect.x, rect.y, rect.width, rect.height, config.startColor, config.endColor);
        } else {
            RenderUtil.renderCapsuleVertical(gui, rect.x, rect.y, rect.width, rect.height, config.startColor, config.endColor);
        }
    }
}
