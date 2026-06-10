package com.pug523.shelf.config;

import java.io.File;
import java.util.function.Supplier;

import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import net.fabricmc.loader.api.FabricLoader;

public class TomlConfigManager<T> implements ConfigManager<T> {
    private final Class<T> configClass;
    private final File configFile;
    private final Supplier<T> defaultSupplier;
    private T config;

    public TomlConfigManager(Class<T> configClass, String modId, String fileName, Supplier<T> defaultSupplier) {
        this.configClass = configClass;
        this.defaultSupplier = defaultSupplier;
        this.configFile = FabricLoader.getInstance().getConfigDir()
                .resolve(modId)
                .resolve(fileName)
                .toFile();
        this.config = defaultSupplier.get();
    }

    @Override
    public T getConfig() {
        return this.config;
    }

    @Override
    public void load() {
        if (!configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
        }

        try (CommentedFileConfig fileConfig = CommentedFileConfig.builder(configFile).build()) {
            if (configFile.exists()) {
                fileConfig.load();
                ObjectConverter converter = new ObjectConverter();
                this.config = converter.toObject(fileConfig, configClass);

                if (this.config == null) {
                    this.config = defaultSupplier.get();
                    save();
                }
            } else {
                this.config = defaultSupplier.get();
                save();
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.config = defaultSupplier.get();
            save();
        }
    }

    @Override
    public void save() {
        if (!configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
        }

        try (CommentedFileConfig fileConfig = CommentedFileConfig.builder(configFile).build()) {
            ObjectConverter converter = new ObjectConverter();
            converter.toConfig(this.config, fileConfig);
            fileConfig.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
