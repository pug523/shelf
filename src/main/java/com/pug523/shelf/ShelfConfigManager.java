package com.pug523.shelf;

import com.pug523.shelf.config.ConfigManager;
import com.pug523.shelf.config.ConfigUtil;
import com.pug523.shelf.config.TomlConfigManager;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ShelfConfigManager {
    @Nullable private static ConfigManager<ShelfConfig> CONFIG = null;

    public static void init() {
        CONFIG = new TomlConfigManager<>(
            ConfigUtil.resolveConfigFile(Shelf.MOD_ID, "config.toml"), ShelfConfig::createDefault,
            ShelfConfigMigrator.migrator);
        CONFIG.load();
    }

    @NonNull
    public static ConfigManager<ShelfConfig> getConfigManager() {
        if (CONFIG == null) {
            init();
        }
        return CONFIG;
    }

    @NonNull
    public static ShelfConfig getConfig() {
        return getConfigManager().getConfig();
    }

    public static void load() {
        getConfigManager().load();
    }

    public static void save() {
        getConfigManager().save();
    }
}
