package com.pug523.shelf.api.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.pug523.shelf.compat.ScreenCompat;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.Function;

import static com.pug523.shelf.command.CommandUtil.literal;

/// A generic command definition that opens a configuration GUI screen.
public class ConfigOpenCommand implements Command {
    private final String baseCommandName;
    private final String subCommandName;
    private final Function<Screen, Screen> screenCreator;

    /// Configures the literal names for the base command structure.
    public ConfigOpenCommand(String baseCommandName, String subCommandName, Function<Screen, Screen> screenCreator) {
        this.baseCommandName = baseCommandName;
        this.subCommandName = subCommandName;
        this.screenCreator = screenCreator;
    }

    public ConfigOpenCommand(String baseCommandName, Function<Screen, Screen> screenCreator) {
        this(baseCommandName, "config", screenCreator);
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
            literal(this.baseCommandName)
                .then(
                    literal(this.subCommandName)
                        .executes(this::executeOpen)
                )
        );
    }

    private int executeOpen(CommandContext<FabricClientCommandSource> context) {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            Screen current = ScreenCompat.getScreen(mc);
            ScreenCompat.setScreen(mc, screenCreator.apply(current));
        });
        return 1;
    }
}
