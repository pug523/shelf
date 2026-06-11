package com.pug523.shelf;

import com.pug523.shelf.gui.layout.LayoutConfig;

public class ShelfConfig {
    public boolean autoRestock = false;
    public int fov = 90;
    public int renderDistance = 32;
    public double volume = 100.0d;
    public float sensitivity = 1.0f;
    public boolean vsync = false;
    public LayoutConfig layoutConfig = LayoutConfig.defaultConfig;

    public transient static final ShelfConfig defaultConfig = new ShelfConfig();
}
