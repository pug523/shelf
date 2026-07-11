package com.pug523.shelf.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import com.pug523.shelf.Shelf;
import com.pug523.shelf.compat.ScreenCompat;
import com.pug523.shelf.ShelfConfigScreen;

import net.minecraft.client.Minecraft;

import static net.minecraft.commands.Commands.literal;

public class ShelfCommand {
    public static void register() {
        // @formatter:off
        //#if MC >= 11900
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
        //#else
        //$$ CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
        //#endif
        // @formatter:on
            dispatcher.register(
                literal(Shelf.MOD_ID)
                    .then(literal("config")
                        .executes(context -> {
                            Minecraft mc = Minecraft.getInstance();
                            mc.execute(() -> ScreenCompat.setScreen(mc, ShelfConfigScreen.createConfigScreen(ScreenCompat.getScreen(mc))));
                            // context.getSource().sendSuccess(() -> ComponentCompat.literal("Opened Shelf Config"), false);
                            return 1;
                        })
                    )
            );
        });
    }
}
