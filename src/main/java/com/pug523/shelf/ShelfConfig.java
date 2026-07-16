package com.pug523.shelf;

import com.mojang.blaze3d.platform.InputConstants;
import com.pug523.shelf.api.annotation.ConfigEntry;
import com.pug523.shelf.api.annotation.WidgetTypes;
import com.pug523.shelf.gui.layout.LayoutConfig;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShelfConfig implements Serializable {
    private static final String O = "shelf.mod.config.option.";
    private static final String D = "debug.";

    private static final String OD = O + D;
    private static final String DS = O + "debug_settings";

    @ConfigEntry(key = OD + "boolean", category = DS, group = OD + "primitive")
    @WidgetTypes.Toggle(WidgetTypes.Toggle.Style.CAPSULE)
    public boolean debugBoolean = false;

    @ConfigEntry(key = OD + "integer", category = DS, group = OD + "primitive")
    @WidgetTypes.SliderInt(min = 0, max = 168)
    public int debugInteger = 3;

    @ConfigEntry(key = OD + "float", category = DS, group = OD + "primitive")
    @WidgetTypes.SliderFloat(min = -10.0f, max = 10.0f, step = 0.01f)
    public float debugFloat = 1.68f;

    @ConfigEntry(key = OD + "double", category = DS, group = OD + "primitive")
    @WidgetTypes.SliderDouble(min = 0.0d, max = 100.0d, step = 0.01d)
    public double debugDouble = 8.000f;

    @ConfigEntry(key = OD + "string", category = DS, group = OD + "primitive")
    @WidgetTypes.InputString
    public String debugString = "debug string";

    @ConfigEntry(key = OD + "item", category = DS, group = OD + "primitive")
    @WidgetTypes.InputItem
    public Item debugItem = Items.APPLE;


    // public Optional<Item> debugOptionalItem = Optional.empty();

    public enum DebugEnum {
        EnumMember1,
        EnumMember2,
        EnumMember3,
    }

    @ConfigEntry(key = OD + "enum", category = DS, group = OD + "primitive")
    @WidgetTypes.Cycling
    public DebugEnum debugEnum = DebugEnum.EnumMember1;

    @ConfigEntry(key = OD + "keybind", category = DS, group = OD + "primitive")
    @WidgetTypes.Keybind
    public List<InputConstants.Key> debugKeybind = new ArrayList<>();

    @ConfigEntry(key = OD + "string_list", category = DS, group = OD + "primitive")
    @WidgetTypes.List(WidgetTypes.InputString.class)
    public List<String> debugStringList = new ArrayList<>();

    @ConfigEntry(key = OD + "integer_list", category = DS, group = OD + "primitive")
    @WidgetTypes.List(WidgetTypes.SliderInt.class)
    public List<Integer> debugIntegerList = new ArrayList<>();


    @ConfigEntry(key = O + "layout")
    public LayoutConfig layoutConfig = LayoutConfig.createDefault();


    public static ShelfConfig createDefault() {
        return new ShelfConfig();
    }
}
