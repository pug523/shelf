package com.pug523.shelf.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.function.Supplier;

public class JsonConfigManager<T extends Serializable> implements ConfigManager<T> {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Class<T> configClass;
    private final File configFile;
    private final Supplier<T> defaultSupplier;
    private T config;

    public JsonConfigManager(Class<T> configClass, File configFile, Supplier<T> defaultSupplier) {
        this.configClass = configClass;
        this.defaultSupplier = defaultSupplier;
        this.configFile = configFile;
        this.config = defaultSupplier.get();
    }

    public JsonConfigManager(Class<T> configClass, String configDirectory, String fileName,
            Supplier<T> defaultSupplier) {
        this(configClass, resolveConfigFile(configDirectory, fileName), defaultSupplier);
    }

    private static File resolveConfigFile(String dir, String file) {
        return FabricLoader.getInstance().getConfigDir().resolve(dir).resolve(file).toFile();
    }

    @Override
    public T getConfig() {
        return this.config;
    }

    @Override
    public void load() {
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                JsonObject root = GSON.fromJson(reader, JsonObject.class);
                this.config = GSON.fromJson(root, configClass);

                if (this.config == null) {
                    this.config = defaultSupplier.get();
                    save();
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.config = defaultSupplier.get();
                save();
            }
        } else {
            this.config = defaultSupplier.get();
            save();
        }
    }

    @Override
    public void save() {
        if (!configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
        }

        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(this.config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
