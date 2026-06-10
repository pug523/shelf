package com.pug523.shelf;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import com.pug523.shelf.config.ConfigManager;
import com.pug523.shelf.config.TomlConfigManager;

//#if MC >= 11802
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
//#else
//$$ import org.apache.logging.log4j.LogManager;
//$$ import org.apache.logging.log4j.Logger;
//#endif

public class Shelf implements ClientModInitializer {
	public static final String MOD_ID = "shelf";
	public static final String MOD_NAME = "Shelf";
	public static String VERSION = "unknown";

    public static final Logger LOGGER =
        //#if MC >= 11802
        LogUtils.getLogger();
        //#else
        //$$ LogManager.getLogger();
        //#endif

    public static ConfigManager<ShelfConfig> CONFIG = null;

    @Override
    public void onInitializeClient() {
        VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(RuntimeException::new).getMetadata().getVersion().getFriendlyString();

        CONFIG = new TomlConfigManager(ShelfConfig.class, Shelf.MOD_ID, "config.toml", ShelfConfig::new);
        CONFIG.load();
    }
}
