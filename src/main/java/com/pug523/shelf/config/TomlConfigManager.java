package com.pug523.shelf.config;

import java.io.File;
import java.io.Serializable;
import java.util.function.Supplier;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.serde.ObjectDeserializer;
import com.electronwill.nightconfig.core.serde.ObjectSerializer;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.pug523.shelf.Shelf;

public class TomlConfigManager<T extends Serializable> implements ConfigManager<T> {
    private final File configFile;
    private final Supplier<T> defaultSupplier;
    private final Migrator migrator;
    private final ObjectSerializer serializer;
    private final ObjectDeserializer deserializer;

    private T config;

    public TomlConfigManager(File configFile, Supplier<T> defaultSupplier, Migrator migrator, ObjectSerializer serializer, ObjectDeserializer deserializer) {
        this.configFile = configFile;
        this.defaultSupplier = defaultSupplier;
        this.migrator = migrator;
        this.serializer = serializer;
        this.deserializer = deserializer;

        this.config = defaultSupplier.get();
    }

    public TomlConfigManager(File configFile, Supplier<T> defaultSupplier, Migrator migrator) {
        this(configFile, defaultSupplier, migrator, ConfigUtil.createSerializer(), ConfigUtil.createDeserializer());
    }

    @Override
    public T getConfig() {
        return config;
    }

    @Override
    public void load() {
        if (!configFile.exists()) {
            config = defaultSupplier.get();
            save();
            return;
        }

        try (CommentedFileConfig fileConfig = CommentedFileConfig.builder(configFile, TomlFormat.instance()).build()) {
            fileConfig.load();

            T defaultObj = defaultSupplier.get();
            boolean migrated = migrator.migrate(fileConfig, defaultObj, serializer);

            config = defaultSupplier.get();
            deserializer.deserializeFields(fileConfig, config);

            if (migrated) {
                save();
            }
        } catch (Exception e) {
            Shelf.LOGGER.error(
                "Failed to parse user config from toml cleanly. Reverting to default config.\nfile: {}\nmessage: {}",
                configFile.getName(), e.getMessage());
            config = defaultSupplier.get();
            save();
        }
    }

    @Override
    public void save() {
        File parent = configFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (CommentedFileConfig fileConfig = CommentedFileConfig.builder(configFile, TomlFormat.instance()).build()) {
            serializer.serializeFields(config, fileConfig);
            fileConfig.save();
        } catch (Exception e) {
            Shelf.LOGGER.error("Failed to save user config to toml.\nfile: {}\nmessage: {}", configFile.getName(),
                e.getMessage());
        }
    }
}
