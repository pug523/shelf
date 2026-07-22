package com.pug523.shelf;

import com.pug523.shelf.common.compat.ComponentCompat;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/// Only for Shelf mod config screen.
/// Library common text handling is in com.pug523.shelf.ui.text.TextUtil.
public class ShelfTextUtil {
    public static final String MOD_LANG_KEY_PREFIX = Shelf.MOD_ID + ".mod.";
    public static final String CONFIG_LANG_KEY_PREFIX = MOD_LANG_KEY_PREFIX + "config.";
    public static final String CONFIG_OPTION_LANG_KEY_PREFIX = CONFIG_LANG_KEY_PREFIX + "option.";

    public static String confKey(String key) {
        return CONFIG_LANG_KEY_PREFIX + key;
    }

    public static Component confText(String key, ChatFormatting... formatting) {
        return ComponentCompat.translatable(confKey(key)).withStyle(formatting);
    }

    public static String optKey(String key) {
        return CONFIG_OPTION_LANG_KEY_PREFIX + key;
    }

    public static Component optText(String key, ChatFormatting... formatting) {
        return ComponentCompat.translatable(optKey(key)).withStyle(formatting);
    }
}
