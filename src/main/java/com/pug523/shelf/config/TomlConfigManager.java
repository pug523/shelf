package com.pug523.shelf.config;

import java.io.File;
import java.io.Serializable;
import java.util.function.Supplier;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.serde.ObjectDeserializer;
import com.electronwill.nightconfig.core.serde.ObjectDeserializerBuilder;
import com.electronwill.nightconfig.core.serde.ObjectSerializer;
import com.pug523.shelf.Shelf;

import net.fabricmc.loader.api.FabricLoader;

public class TomlConfigManager<T extends Serializable> implements ConfigManager<T> {

    private final Class<T> configClass;
    private final File configFile;
    private final Supplier<T> defaultSupplier;

    private final ObjectSerializer serializer = ObjectSerializer.standard();
    private final ObjectDeserializer deserializer = createCustomDeserializer();

    private T config;

    public TomlConfigManager(Class<T> configClass, File configFile, Supplier<T> defaultSupplier) {
        this.configClass = configClass;
        this.configFile = configFile;
        this.defaultSupplier = defaultSupplier;
        this.config = defaultSupplier.get();
    }

    public TomlConfigManager(Class<T> configClass, String configDirectory, String fileName,
            Supplier<T> defaultSupplier) {
        this(configClass, resolveConfigFile(configDirectory, fileName), defaultSupplier);
    }

    private static File resolveConfigFile(String dir, String file) {
        return FabricLoader.getInstance().getConfigDir().resolve(dir).resolve(file).toFile();
    }

    private static ObjectDeserializer createCustomDeserializer() {
        ObjectDeserializerBuilder builder = ObjectDeserializer.builder();

        // Double (TOML float) -> Float
        builder.withDeserializerForClass(Double.class, Float.class, (value, constraint, context) -> value.floatValue());

        // Long (TOML int) -> Integer
        builder.withDeserializerForClass(Long.class, Integer.class, (value, constraint, context) -> value.intValue());

        // Long (TOML int) -> Double
        builder.withDeserializerForClass(Long.class, Double.class, (value, constraint, context) -> value.doubleValue());

        // Long (TOML int) -> Float
        builder.withDeserializerForClass(Long.class, Float.class, (value, constraint, context) -> value.floatValue());

        return builder.build();
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

        try (CommentedFileConfig fileConfig = CommentedFileConfig.builder(configFile).build()) {

            fileConfig.load();

            config = defaultSupplier.get();
            deserializer.deserializeFields(fileConfig, config);
        } catch (Exception e) {
            Shelf.LOGGER.error(
                    "Failed to parse user config from toml cleanly (maybe due to an invalid format or typo). Reverting to default config.",
                    e.getMessage());
            Shelf.LOGGER.error("file: {}", configFile.getName());
            Shelf.LOGGER.error("message: {}", e.getMessage());
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

        try (CommentedFileConfig fileConfig = CommentedFileConfig.builder(configFile).build()) {

            serializer.serializeFields(config, fileConfig);
            fileConfig.save();
        } catch (Exception e) {
            Shelf.LOGGER.error("Failed to save user config to toml.");
            Shelf.LOGGER.error("file: {}", configFile.getName());
            Shelf.LOGGER.error("message: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
