package com.pug523.shelf;

import static com.pug523.shelf.ShelfTextUtil.confText;

import com.pug523.shelf.ui.screen.ConfigScreen;
import com.pug523.shelf.api.annotation.AnnotationParser;

import net.minecraft.client.gui.screens.Screen;

public class ShelfConfigScreen {
    public static ConfigScreen createConfigScreen(Screen parent) {
        return AnnotationParser.buildScreen(
            confText("title"),
            parent,
            ShelfConfigManager.getConfig(),
            ShelfConfig.createDefault(),
            ShelfConfigManager::save,
            ShelfConfigManager.getConfig().layoutConfig
        );
    }
}
