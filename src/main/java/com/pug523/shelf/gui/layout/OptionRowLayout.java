package com.pug523.shelf.gui.layout;

public class OptionRowLayout {
    public final boolean isHeader;
    public final Bounds rowBounds;
    public final int textX;
    public final int textY;
    public final Bounds resetButtonBounds;

    public OptionRowLayout(boolean isHeader, Bounds rowBounds, int textX, int textY, Bounds resetButtonBounds) {
        this.isHeader = isHeader;
        this.rowBounds = rowBounds;
        this.textX = textX;
        this.textY = textY;
        this.resetButtonBounds = resetButtonBounds;
    }
}
