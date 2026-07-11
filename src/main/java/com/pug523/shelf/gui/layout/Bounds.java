package com.pug523.shelf.gui.layout;

public class Bounds {
    public final int x, y, width, height;
    public final int maxX, maxY;

    public Bounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.maxX = x + width;
        this.maxY = y + height;
    }

    public boolean contains(double mouseX, double mouseY) {
        return mouseX >= x && mouseX < maxX && mouseY >= y && mouseY < maxY;
    }

    @Override
    public String toString() {
        return String.format("[Bounds] x: %d, y: %d, width: %d, height: %d, maxX: %d, maxY: %d", x, y, width, height,
                maxX, maxY);
    }
}
