package com.pug523.shelf.api.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

@FunctionalInterface
public interface Command {
    void register(CommandDispatcher<FabricClientCommandSource> dispatcher);
}
