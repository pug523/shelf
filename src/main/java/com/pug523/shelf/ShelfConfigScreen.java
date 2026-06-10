package com.pug523.shelf;

import java.util.ArrayList;
import java.util.List;

import com.pug523.shelf.config.Profile;
import com.pug523.shelf.config.Option;

import com.pug523.shelf.gui.ConfigScreen;
import com.pug523.shelf.gui.OptionGroup;
import com.pug523.shelf.gui.TabNode;
import com.pug523.shelf.gui.widget.BooleanOptionWidget;
import com.pug523.shelf.gui.widget.OptionWidget;
import com.pug523.shelf.gui.widget.SliderOptionWidget;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ShelfConfigScreen {
    public static ConfigScreen createConfigScreen(Screen parent) {
        ShelfConfig config = Shelf.CONFIG.getConfig();
        ShelfConfig defaultConfig = ShelfConfig.defaultConfig;

        List<TabNode> roots = new ArrayList<>();

        TabNode masterRootNode = new TabNode(categoryText("all_settings"));

        TabNode generalNode = new TabNode(categoryText("general"))
            .addGroup(new OptionGroup(categoryText("core"), List.of(
                BooleanOptionWidget.of(new Option<>(key("option.auto_restock"), defaultConfig.autoRestock, () -> config.autoRestock, (v) -> config.autoRestock = v, List.of()), false),
                SliderOptionWidget.ofFloat(new Option<>(key("option.sensitivity"), defaultConfig.sensitivity, () -> config.sensitivity, (v) -> config.sensitivity = v, List.of()), 0.1f, 2.0f, 0.1f, false)
            )));

        TabNode videoNode = new TabNode(categoryText("video"));

        TabNode displayNode = new TabNode(categoryText("display"))
            .addGroup(new OptionGroup(categoryText("screen"), List.of(
                SliderOptionWidget.ofInt(new Option<>(key("option.fov"), defaultConfig.fov, () -> config.fov, (v) -> config.fov = v, List.of()), 30, 110, 1, false)
            )));

        TabNode performanceNode = new TabNode(categoryText("performance"))
            .addGroup(new OptionGroup(categoryText("graphics"), List.of(
                SliderOptionWidget.ofInt(new Option<>(key("option.render_distance"), defaultConfig.renderDistance, () -> config.renderDistance = v, (v) -> config.renderDistance = v, List.of()), 2, 32, 1, false)
            )));

        TabNode advancedVideoTweaks = new TabNode(categoryText("advanced_tweaks"))
            .addGroup(new OptionGroup(categoryText("experimental_shaders"), List.of(
                BooleanOptionWidget.of(new Option<>(key("option.vsync"), defaultConfig.vsync, () -> config.vsync, (v) -> config.vsync = v, List.of()), false)
            )));

        // Link them up recursively.
        performanceNode.addNode(advancedVideoTweaks);
        videoNode.addNode(displayNode);
        videoNode.addNode(performanceNode);

        masterRootNode.addNode(generalNode);
        masterRootNode.addNode(videoNode);

        roots.add(masterRootNode);

        List<Profile> profiles = new ArrayList<>();
        return new ConfigScreen(text("title"), parent, roots, profiles, Shelf.CONFIG::save);
    }

    private static String key(String languageKey) {
        return Shelf.MOD_ID + ".mod.config." + languageKey;
    }

    private static Component text(String languageKey) {
        return Component.translatable(key(languageKey));
    }

    private static Component categoryText(String categoryKey) {
        return Component.translatable(key("category." + categoryKey));
    }
}
