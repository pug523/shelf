package com.pug523.shelf;

import com.pug523.shelf.api.command.Command;
import com.pug523.shelf.api.command.ConfigOpenCommand;
import com.pug523.shelf.api.command.ConfigControlCommand;
import com.pug523.shelf.command.CommandManager;

import com.pug523.shelf.gui.renderer.shader.ShaderManager;

public class InitHandler {
    public static void init() {
        ShelfConfigManager.init();
        initCommands();
        ShaderManager.registerEvent();
    }

    private static void initCommands() {
        Command openCommand = new ConfigOpenCommand(Shelf.MOD_ID, "config", ShelfConfigScreen::createConfigScreen);
        Command controllCommand = new ConfigControlCommand.Builder<>
            (Shelf.MOD_ID, ShelfConfigManager.getConfig(), ShelfConfig.createDefault())
            .subCommandName("config")
            .onUpdate(ShelfConfigManager::save)
            .build();

        CommandManager.registerCommand(openCommand);
        CommandManager.registerCommand(controllCommand);

        CommandManager.registerEvent();
    }
}
