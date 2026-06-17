package com.pug523.shelf.gui.layout;

import java.util.List;

import com.pug523.shelf.gui.model.OptionContext;
import com.pug523.shelf.gui.model.RenderableItem;

public class LayoutEngine {
    private final LayoutConfig config;

    private int width;
    private int height;

    public int tabAreaWidth;
    public int optionAreaWidth;
    public int descAreaX;
    public int mainContentHeight;

    public LayoutEngine(LayoutConfig config) {
        this.config = config;

    }

    public void rebuild(int width, int height) {
        this.width = width;
        this.height = height;
        this.tabAreaWidth = (int) (width * config.tabAreaWidthPercent);
        this.optionAreaWidth = (int) (width * config.optionAreaWidthPercent);
        this.descAreaX = this.tabAreaWidth + this.optionAreaWidth;
        this.mainContentHeight = height - config.topBarHeight - config.bottomBarHeight;
    }

    public boolean isWithinContentArea(double mouseY) {
        return mouseY > config.topBarHeight && mouseY < (height - config.bottomBarHeight);
    }

    public boolean isMouseOverTabs(double mouseX) {
        return mouseX < tabAreaWidth;
    }

    public boolean isMouseOverOptions(double mouseX) {
        return mouseX >= tabAreaWidth && mouseX < descAreaX;
    }

    public LayoutConfig getConfig() {
        return config;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
