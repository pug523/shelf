package com.pug523.shelf.compat;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

//#if MC <= 11802
//$$ import net.minecraft.network.chat.TextComponent;
//$$ import net.minecraft.network.chat.TranslatableComponent;
//#endif

public class ComponentCompat {
    public static MutableComponent literal(String message) {
        //#if MC >= 11900
        return Component.literal(message);
        //#else
        //$$ return new TextComponent(message);
        //#endif
    }

    public static MutableComponent translatable(String message) {
        //#if MC >= 11900
        return Component.translatable(message);
        //#else
        //$$ return new TranslatableComponent(message);
        //#endif
    }
}
