package com.pug523.shelf.gui.text;

import com.pug523.shelf.Shelf;
import com.pug523.shelf.compat.ComponentCompat;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

// Library common text handling
public class TextUtil {
    public static final String LIB_LANG_KEY_PREFIX = Shelf.MOD_ID + ".lib.";
    public static final String CONFIG_LANG_KEY_PREFIX = LIB_LANG_KEY_PREFIX + "config.";
    public static final String GUI_LANG_KEY_PREFIX = CONFIG_LANG_KEY_PREFIX + "gui.";

    public static String confKey(String key) {
        return CONFIG_LANG_KEY_PREFIX + key;
    }

    public static Component confText(String key, ChatFormatting... formatting) {
        return ComponentCompat.translatable(confKey(key)).withStyle(formatting);
    }

    public static String guiKey(String key) {
        return GUI_LANG_KEY_PREFIX + key;
    }

    public static Component guiText(String key, ChatFormatting... formatting) {
        return ComponentCompat.translatable(guiKey(key)).withStyle(formatting);
    }

    public static int width(Font font, Component text) {
        //#if MC >= 11600
        return font.width(text);
        //#else
        //$$ return font.width(text.getString());
        //#endif
    }

    public static int width(Font font, String text) {
        return font.width(text);
    }
}
