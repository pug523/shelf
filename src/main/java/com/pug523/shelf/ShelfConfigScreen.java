package com.pug523.shelf;

import java.util.ArrayList;
import java.util.List;

import com.pug523.shelf.config.Option;
import com.pug523.shelf.config.Profile;
import com.pug523.shelf.gui.ConfigScreen;
import com.pug523.shelf.gui.OptionGroup;
import com.pug523.shelf.gui.TabNode;
import com.pug523.shelf.gui.layout.LayoutConfig;
import com.pug523.shelf.gui.widget.BooleanOptionWidget;
import com.pug523.shelf.gui.widget.SliderOptionWidget;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ShelfConfigScreen {
    public static ConfigScreen createConfigScreen(Screen parent) {
        ShelfConfig config = Shelf.CONFIG.getConfig();
        ShelfConfig defaultConfig = ShelfConfig.defaultConfig;

        List<TabNode> roots = new ArrayList<>();

        TabNode masterRootNode = new TabNode(categoryText("all_settings"));

        // @formatter:off
        TabNode generalNode = new TabNode(categoryText("general"))
            .addGroup(new OptionGroup(categoryText("core"), List.of(
                new BooleanOptionWidget(new Option<>(opt("general.auto_restock"), defaultConfig.autoRestock, () -> config.autoRestock, (v) -> config.autoRestock = v, List.of()), false),
                SliderOptionWidget.ofFloat(new Option<>(opt("general.sensitivity"), defaultConfig.sensitivity, () -> config.sensitivity, (v) -> config.sensitivity = v, List.of()), 0.1f, 2.0f, 0.1f, false)
            )));

        TabNode videoNode = new TabNode(categoryText("video"));

        TabNode displayNode = new TabNode(categoryText("display"))
            .addGroup(new OptionGroup(categoryText("screen"), List.of(
                SliderOptionWidget.ofInt(new Option<>(opt("display.screen.fov"), defaultConfig.fov, () -> config.fov, (v) -> config.fov = v, List.of()), 30, 110, 1, false)
            )));

        TabNode performanceNode = new TabNode(categoryText("performance"))
            .addGroup(new OptionGroup(categoryText("graphics"), List.of(
                SliderOptionWidget.ofInt(new Option<>(opt("performance.graphics.render_distance"), defaultConfig.renderDistance, () -> config.renderDistance, (v) -> config.renderDistance = v, List.of()), 2, 32, 1, false)
            )));

        TabNode advancedVideoTweaks = new TabNode(categoryText("advanced_tweaks"))
            .addGroup(new OptionGroup(categoryText("experimental_shaders"), List.of(
                new BooleanOptionWidget(new Option<>(opt("advanced_tweaks.experimental_shaders.vsync"), defaultConfig.vsync, () -> config.vsync, (v) -> config.vsync = v, List.of()), false)
            )));
        // @formatter:on

        TabNode layoutNode = layoutNode(config.layoutConfig);

        // Link them up recursively.
        performanceNode.addNode(advancedVideoTweaks);
        videoNode.addNode(displayNode);
        videoNode.addNode(performanceNode);

        masterRootNode.addNode(generalNode);
        masterRootNode.addNode(videoNode);
        masterRootNode.addNode(layoutNode);

        roots.add(masterRootNode);

        List<Profile> profiles = new ArrayList<>();
        LayoutConfig layout = new LayoutConfig();
        return new ConfigScreen(text("title"), parent, roots, profiles, Shelf.CONFIG::save, layout);
    }

    private static TabNode layoutNode(LayoutConfig c) {
        LayoutConfig d = LayoutConfig.defaultConfig;
        // @formatter:off
        return new TabNode(categoryText("layout_settings"))
            .addGroup(new OptionGroup(categoryText("layout.dimensions"), List.of(
                SliderOptionWidget.ofInt(new Option<>(opt("layout.top_bar"), d.topBarHeight, () -> c.topBarHeight, (v) -> c.topBarHeight = v, List.of()), 10, 60, 1, false),
                SliderOptionWidget.ofInt(new Option<>(opt("layout.bottom_bar"), d.bottomBarHeight, () -> c.bottomBarHeight, (v) -> c.bottomBarHeight = v, List.of()), 10, 60, 1, false),
                SliderOptionWidget.ofFloat(new Option<>(opt("layout.tab_width_pct"), (float)d.tabAreaWidthPercent, () -> (float)c.tabAreaWidthPercent, (v) -> c.tabAreaWidthPercent = v, List.of()), 0.1f, 0.4f, 0.01f, false),
                SliderOptionWidget.ofFloat(new Option<>(opt("layout.option_width_pct"), (float)d.optionAreaWidthPercent, () -> (float)c.optionAreaWidthPercent, (v) -> c.optionAreaWidthPercent = v, List.of()), 0.3f, 0.7f, 0.01f, false),
                SliderOptionWidget.ofFloat(new Option<>(opt("layout.tab_scroll_speed"), (float)d.tabScrollSpeed, () -> (float)c.tabScrollSpeed, (v) -> c.tabScrollSpeed = v, List.of()), 1.0f, 50.0f, 0.5f, false),
                SliderOptionWidget.ofFloat(new Option<>(opt("layout.option_scroll_speed"), (float)d.optionScrollSpeed, () -> (float)c.optionScrollSpeed, (v) -> c.optionScrollSpeed = v, List.of()), 1.0f, 50.0f, 0.5f, false)
            )))
            .addGroup(new OptionGroup(categoryText("layout.sizes"), List.of(
                SliderOptionWidget.ofInt(new Option<>(opt("layout.tab_height"), d.tabItemHeight, () -> c.tabItemHeight, (v) -> c.tabItemHeight = v, List.of()), 14, 40, 1, false),
                SliderOptionWidget.ofInt(new Option<>(opt("layout.option_height"), d.optionItemHeight, () -> c.optionItemHeight, (v) -> c.optionItemHeight = v, List.of()), 14, 40, 1, false),
                SliderOptionWidget.ofInt(new Option<>(opt("layout.indent"), d.tabTreeIndentation, () -> c.tabTreeIndentation, (v) -> c.tabTreeIndentation = v, List.of()), 0, 25, 1, false),
                SliderOptionWidget.ofInt(new Option<>(opt("layout.tab_start_offset_y"), d.tabItemStartOffsetY, () -> c.tabItemStartOffsetY, (v) -> c.tabItemStartOffsetY = v, List.of()), 0, 100, 1, false),
                SliderOptionWidget.ofInt(new Option<>(opt("layout.option_start_offset_y"), d.optionItemStartOffsetY, () -> c.optionItemStartOffsetY, (v) -> c.optionItemStartOffsetY = v, List.of()), 0, 100, 1, false),
                SliderOptionWidget.ofInt(new Option<>(opt("layout.text_padding_x"), d.textPaddingX, () -> c.textPaddingX, (v) -> c.textPaddingX = v, List.of()), 0, 30, 1, false),
                SliderOptionWidget.ofInt(new Option<>(opt("layout.reset_btn_width"), d.resetButtonWidth, () -> c.resetButtonWidth, (v) -> c.resetButtonWidth = v, List.of()), 10, 40, 1, false),
                SliderOptionWidget.ofInt(new Option<>(opt("layout.scrollbar_width"), d.scrollbarWidth, () -> c.scrollbarWidth, (v) -> c.scrollbarWidth = v, List.of()), 1, 10, 1, false),
                SliderOptionWidget.ofInt(new Option<>(opt("layout.scrollbar_min_height"), d.scrollbarMinHeight, () -> c.scrollbarMinHeight, (v) -> c.scrollbarMinHeight = v, List.of()), 5, 50, 1, false),
                SliderOptionWidget.ofInt(new Option<>(opt("layout.option_text_offset_x"), d.optionTextOffsetX, () -> c.optionTextOffsetX, (v) -> c.optionTextOffsetX = v, List.of()), 0, 40, 1, false),
                SliderOptionWidget.ofInt(new Option<>(opt("layout.option_header_offset_x"), d.optionHeaderOffsetX, () -> c.optionHeaderOffsetX, (v) -> c.optionHeaderOffsetX = v, List.of()), 0, 40, 1, false),
                SliderOptionWidget.ofInt(new Option<>(opt("layout.desc_text_offset_x"), d.descTextOffsetX, () -> c.descTextOffsetX, (v) -> c.descTextOffsetX = v, List.of()), 0, 40, 1, false),
                SliderOptionWidget.ofInt(new Option<>(opt("layout.desc_text_offset_y"), d.descTextOffsetY, () -> c.descTextOffsetY, (v) -> c.descTextOffsetY = v, List.of()), 0, 40, 1, false),
                SliderOptionWidget.ofInt(new Option<>(opt("layout.desc_text_right_padding"), d.descTextRightPadding, () -> c.descTextRightPadding, (v) -> c.descTextRightPadding = v, List.of()), 0, 50, 1, false)
            )))
            .addGroup(new OptionGroup(categoryText("layout.colors.panels"), List.of(
                // TODO: ColorPickerWidget
                // new ColorPickerWidget(new Option<>(opt("layout.color.screen_bg"), d.colorScreenBaseBackground, () -> c.colorScreenBaseBackground, (v) -> c.colorScreenBaseBackground = v, List.of())),
                // new ColorPickerWidget(new Option<>(opt("layout.color.header_bg"), d.colorHeaderBackground, () -> c.colorHeaderBackground, (v) -> c.colorHeaderBackground = v, List.of())),
                // new ColorPickerWidget(new Option<>(opt("layout.color.footer_bg"), d.colorFooterBackground, () -> c.colorFooterBackground, (v) -> c.colorFooterBackground = v, List.of())),
                // new ColorPickerWidget(new Option<>(opt("layout.color.tab_panel_bg"), d.colorTabPanelBackground, () -> c.colorTabPanelBackground, (v) -> c.colorTabPanelBackground = v, List.of())),
                // new ColorPickerWidget(new Option<>(opt("layout.color.option_panel_bg"), d.colorOptionPanelBackground, () -> c.colorOptionPanelBackground, (v) -> c.colorOptionPanelBackground = v, List.of())),
                // new ColorPickerWidget(new Option<>(opt("layout.color.desc_panel_bg"), d.colorDescriptionPanelBackground, () -> c.colorDescriptionPanelBackground, (v) -> c.colorDescriptionPanelBackground = v, List.of()))
            )))
            .addGroup(new OptionGroup(categoryText("layout.colors.elements"), List.of(
                // TODO: ColorPickerWidget
                // new ColorPickerWidget(new Option<>(opt("layout.color.text_primary"), d.colorTextPrimary, () -> c.colorTextPrimary, (v) -> c.colorTextPrimary = v, List.of())),
                // new ColorPickerWidget(new Option<>(opt("layout.color.text_secondary"), d.colorTextSecondary, () -> c.colorTextSecondary, (v) -> c.colorTextSecondary = v, List.of())),
                // new ColorPickerWidget(new Option<>(opt("layout.color.text_muted"), d.colorTextMuted, () -> c.colorTextMuted, (v) -> c.colorTextMuted = v, List.of())),
                // new ColorPickerWidget(new Option<>(opt("layout.color.text_disabled"), d.colorTextDisabled, () -> c.colorTextDisabled, (v) -> c.colorTextDisabled = v, List.of())),
                // new ColorPickerWidget(new Option<>(opt("layout.color.item_selected_text"), d.colorItemSelectedText, () -> c.colorItemSelectedText, (v) -> c.colorItemSelectedText = v, List.of())),
                // new ColorPickerWidget(new Option<>(opt("layout.color.item_unselected_text"), d.colorItemUnselectedText, () -> c.colorItemUnselectedText, (v) -> c.colorItemUnselectedText = v, List.of())),
                // new ColorPickerWidget(new Option<>(opt("layout.color.item_hover_bg"), d.colorItemHoverBackground, () -> c.colorItemHoverBackground, (v) -> c.colorItemHoverBackground = v, List.of())),
                // new ColorPickerWidget(new Option<>(opt("layout.color.item_selected_bg"), d.colorItemSelectedBackground, () -> c.colorItemSelectedBackground, (v) -> c.colorItemSelectedBackground = v, List.of())),
                // new ColorPickerWidget(new Option<>(opt("layout.color.scrollbar_track"), d.colorScrollBarTrack, () -> c.colorScrollBarTrack, (v) -> c.colorScrollBarTrack = v, List.of())),
                // new ColorPickerWidget(new Option<>(opt("layout.color.scrollbar_thumb"), d.colorScrollBarThumb, () -> c.colorScrollBarThumb, (v) -> c.colorScrollBarThumb = v, List.of()))
            )));
        // @formatter:on
    }

    private static String key(String languageKey) {
        return Shelf.MOD_ID + ".mod.config." + languageKey;
    }

    private static String opt(String languageKey) {
        return key("option." + languageKey);
    }

    private static Component text(String languageKey) {
        return Component.translatable(key(languageKey));
    }

    private static Component categoryText(String categoryKey) {
        return Component.translatable(key("category." + categoryKey));
    }
}
