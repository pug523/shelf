package com.pug523.shelf;

public class ShelfConfig {
    public Boolean autoRestock = false;
    public Integer fov = 90;
    public Integer renderDistance = 32;
    public Double volume = 100.0d;
    public Float sensitivity = 1.0f;
    public Boolean vsync = false;

    public static final ShelfConfig defaultConfig = new ShelfConfig();
}
