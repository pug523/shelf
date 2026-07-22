package com.pug523.shelf.core.geometry;

public class Rect {
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public Rect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int maxX() {
        return x + width;
    }

    public int maxY() {
        return y + height;
    }

    public boolean contains(int x, int y) {
        return this.x <= x && x <= this.x + width
            && this.y <= y && y <= this.y + height;
    }

    public boolean contains(double x, double y) {
        return this.x <= x && x <= this.x + width
            && this.y <= y && y <= this.y + height;
    }
}
