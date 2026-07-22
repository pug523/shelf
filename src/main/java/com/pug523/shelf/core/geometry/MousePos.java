package com.pug523.shelf.core.geometry;

public class MousePos {
    public final double x;
    public final double y;

    MousePos(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean isHovering(Rect rect) {
        return rect.contains(x, y);
    }
}
