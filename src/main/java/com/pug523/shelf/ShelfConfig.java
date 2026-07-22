package com.pug523.shelf;

import com.mojang.blaze3d.platform.InputConstants;
import com.pug523.shelf.api.annotation.ConfigEntry;
import com.pug523.shelf.api.annotation.WidgetTypes;
import com.pug523.shelf.core.Colors;
import com.pug523.shelf.ui.layout.LayoutConfig;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ShelfConfig implements Serializable {
    private static final String DEBUG_CAT = "shelf.mod.config.option.debug";
    private static final String LAYOUT_CAT = "shelf.mod.config.option.layout";

    @ConfigEntry(key = "boolean", category = DEBUG_CAT, group = "primitive")
    @WidgetTypes.Toggle(WidgetTypes.Toggle.Style.CAPSULE)
    public boolean debugBoolean = false;

    @ConfigEntry(key = "action_button_boolean", category = DEBUG_CAT, group = "primitive")
    @WidgetTypes.Toggle(WidgetTypes.Toggle.Style.ACTION_BUTTON)
    public boolean debugActionButtonBoolean = false;

    @ConfigEntry(key = "box_boolean", category = DEBUG_CAT, group = "primitive")
    @WidgetTypes.Toggle(WidgetTypes.Toggle.Style.BOX)
    public boolean debugBoxBoolean = false;

    @ConfigEntry(key = "integer", category = DEBUG_CAT, group = "primitive")
    @WidgetTypes.SliderInt(min = 0, max = 168)
    public int debugInteger = 3;

    @ConfigEntry(key = "float", category = DEBUG_CAT, group = "primitive")
    @WidgetTypes.SliderFloat(min = -10.0f, max = 10.0f, step = 0.01f)
    public float debugFloat = 1.68f;

    @ConfigEntry(key = "double", category = DEBUG_CAT, group = "primitive")
    @WidgetTypes.SliderDouble(min = 0.0d, max = 100.0d, step = 0.01d)
    public double debugDouble = 8.000f;

    @ConfigEntry(key = "string", category = DEBUG_CAT, group = "primitive")
    @WidgetTypes.InputString
    public String debugString = "debug string";

    @ConfigEntry(key = "item", category = DEBUG_CAT, group = "primitive")
    @WidgetTypes.InputItem
    public Item debugItem = Items.APPLE;


    // public Optional<Item> debugOptionalItem = Optional.empty();

    public enum DebugEnum {
        EnumMember1,
        EnumMember2,
        EnumMember3,
    }

    @ConfigEntry(key = "enum", category = DEBUG_CAT, group = "primitive")
    @WidgetTypes.Cycling
    public DebugEnum debugEnum = DebugEnum.EnumMember1;

    @ConfigEntry(key = "keybind", category = DEBUG_CAT, group = "primitive")
    @WidgetTypes.Keybind
    public List<InputConstants.Key> debugKeybind = new ArrayList<>();

    @ConfigEntry(key = "string_list", category = DEBUG_CAT, group = "primitive")
    @WidgetTypes.List(WidgetTypes.InputString.class)
    public List<String> debugStringList = new ArrayList<>();

    @ConfigEntry(key = "integer_list", category = DEBUG_CAT, group = "primitive")
    @WidgetTypes.List(WidgetTypes.SliderInt.class)
    public List<Integer> debugIntegerList = new ArrayList<>();

    @ConfigEntry(key = "color", category = DEBUG_CAT, group = "primitive")
    @WidgetTypes.ColorPicker
    public int debugColor = Colors.GREEN1;

    @ConfigEntry(key = "selector_enum", category = DEBUG_CAT, group = "primitive")
    @WidgetTypes.Selector(enumClass = DebugEnum.class)
    public DebugEnum debugSelectorEnum = DebugEnum.EnumMember2;

    public static class MultiSelectorCandidatesSupplier implements Supplier<List<String>> {
        @Override
        public List<String> get() {
            return Arrays.asList("minecraft:dirt", "minecraft:stone", "minecraft:diamond_block", "minecraft:bedrock");
        }
    }

    @ConfigEntry(key = "multi_selector_string_list", category = DEBUG_CAT, group = "primitive")
    @WidgetTypes.MultiSelector(candidates = MultiSelectorCandidatesSupplier.class)
    public List<String> debugMultiSelectorStringList = new ArrayList<>();

    @ConfigEntry(key = LAYOUT_CAT)
    public LayoutConfig layoutConfig = LayoutConfig.createDefault();


    public static ShelfConfig createDefault() {
        return new ShelfConfig();
    }
}
