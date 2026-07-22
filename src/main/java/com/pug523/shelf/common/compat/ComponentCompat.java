package com.pug523.shelf.common.compat;

import com.pug523.shelf.ShelfTranslator;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

//#if MC < 11900
//$$ import net.minecraft.network.chat.TextComponent;
//$$ import net.minecraft.network.chat.TranslatableComponent;
//#endif

//#if MC >= 11600
import net.minecraft.network.chat.MutableComponent;
//#endif

public class ComponentCompat {
    // @formatter:off
    //#if MC >= 11600
    public static MutableComponent literal(String message) {
    //#else
    //$$ public static Component literal(String message) {
    //#endif
    // @formatter:on
        //#if MC >= 11900
        return Component.literal(message);
        //#else
        //$$ return new TextComponent(message);
        //#endif
    }

    // @formatter:off
    //#if MC >= 11600
    public static MutableComponent translatable(String message) {
    //#else
    //$$ public static Component translatable(String message) {
    //#endif
    // @formatter:on
        return literal(ShelfTranslator.engine().translate(message));
        /*
        //#if MC >= 11900
        return Component.translatable(message);
        //#else
        //$$ return new TranslatableComponent(message);
        //#endif
        */
    }

    // @formatter:off
    //#if MC >= 11600
    public static MutableComponent empty() {
    //#else
    //$$ public static Component empty() {
    //#endif
    // @formatter:on
        return literal("");
    }

    public static int width(Font font, Component text) {
        //#if MC >= 11600
        return font.width(text);
        //#else
        //$$ return font.width(text.getString());
        //#endif
    }

    public static Component withColor(MutableComponent component, int color) {
        //#if MC >= 12002
        return component.withColor(color);
        //#else
        //$$ return component.withStyle(component.getStyle().withColor(color));
        //#endif
    }

    public static int width(Font font, String text) {
        return font.width(text);
    }
}
