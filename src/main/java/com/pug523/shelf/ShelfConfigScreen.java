package com.pug523.shelf;

import java.util.ArrayList;
import java.util.List;

import com.pug523.shelf.gui.ConfigScreen;
import com.pug523.shelf.gui.Option;
import com.pug523.shelf.gui.OptionGroup;
import com.pug523.shelf.gui.Profile;
import com.pug523.shelf.gui.TabNode;
import com.pug523.shelf.gui.widget.BooleanOptionWidget;
import com.pug523.shelf.gui.widget.SliderOptionWidget;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ShelfConfigScreen {
    public static ConfigScreen createConfigScreen(Screen parent) {
        ShelfConfig config = ShelfConfigManager.config;

        List<TabNode> roots = new ArrayList<>();

        TabNode masterRootNode = new TabNode(categoryText("all_settings"));

        TabNode generalNode = new TabNode(categoryText("general"))
            .addGroup(new OptionGroup(categoryText("core"), List.of(
                new Option<>(key("option.auto_restock"), new BooleanOptionWidget(() -> config.autoRestock, (v) -> config.autoRestock = v, false)),
                new Option<>(key("option.sensitivity"), SliderOptionWidget.ofFloat(() -> config.sensitivity, (v) -> config.sensitivity = v, 0.1f, 2.0f, 0.1f, false))
            )));

        TabNode videoNode = new TabNode(categoryText("video"));

        TabNode displayNode = new TabNode(categoryText("display"))
            .addGroup(new OptionGroup(categoryText("screen"), List.of(
                new Option<>(key("option.fov"), SliderOptionWidget.ofInt(() -> config.fov, (val) -> config.fov = val, 30, 110, 1, false))
            )));

        TabNode performanceNode = new TabNode(categoryText("performance"))
            .addGroup(new OptionGroup(categoryText("graphics"), List.of(
                new Option<>(key("option.render_distance"), SliderOptionWidget.ofInt(() -> config.renderDistance, (v) -> config.renderDistance = v, 2, 32, 1, false))
            )));

        TabNode advancedVideoTweaks = new TabNode(categoryText("advanced_tweaks"))
            .addGroup(new OptionGroup(categoryText("experimental_shaders"), List.of(
                new Option<>(key("option.vsync"), new BooleanOptionWidget(() -> config.vsync, (v) -> config.vsync = v, false))
            )));

        // Link them up recursively.
        performanceNode.addNode(advancedVideoTweaks);
        videoNode.addNode(displayNode);
        videoNode.addNode(performanceNode);

        masterRootNode.addNode(generalNode);
        masterRootNode.addNode(videoNode);

        roots.add(masterRootNode);

        List<Profile> profiles = new ArrayList<>();
        return new ConfigScreen(text("title"), parent, roots, profiles, ShelfConfigManager::save);
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
