package com.pug523.shelf;

import com.pug523.shelf.gui.layout.LayoutConfig;

import net.minecraft.world.item.Item;

import java.io.Serializable;

public class ShelfConfig implements Serializable {
    public boolean autoEat = false;
    public boolean autoRestock = false;
    public Item restockItem = null;
    public int fov = 90;
    public int renderDistance = 32;
    public double volume = 100.0d;
    public float sensitivity = 1.0f;
    public boolean vsync = false;
    public LayoutConfig layoutConfig = LayoutConfig.createDefault();

    public static final ShelfConfig createDefault() {
        return new ShelfConfig();
    }
}
