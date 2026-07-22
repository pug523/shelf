package com.pug523.shelf.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.pug523.shelf.api.command.Command;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import java.util.ArrayList;
import java.util.List;

//#if MC >= 11900
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
//#else
//$$ import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
//#endif

public class CommandManager {
    private static final List<Command> COMMANDS = new ArrayList<>();

    public static void registerCommand(Command command) {
        COMMANDS.add(command);
    }

    public static void registerEvent() {
        //#if MC >= 11900
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            registerCommandsToDispatcher(dispatcher);
        });
        //#else
        //$$ registerCommandsToDispatcher(ClientCommandManager.DISPATCHER);
        //#endif
    }

    private static void registerCommandsToDispatcher(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        for (Command command : COMMANDS) {
            command.register(dispatcher);
        }
    }
}
