package com.pug523.shelf.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.serde.ObjectSerializer;

import java.util.HashMap;
import java.util.Map;

public class Migrator {
    private Map<String, String> renameMap = new HashMap<>();

    public Migrator addRenameRule(String oldKey, String newKey) {
        this.renameMap.put(oldKey, newKey);
        return this;
    }

    public void setRenameMap(Map<String, String> renameMap) {
        this.renameMap = renameMap;
    }

    public boolean migrate(Config fileConfig, Object defaultObj, ObjectSerializer serializer) {
        boolean anyMigrated = false;
        for (Map.Entry<String, String> entry : renameMap.entrySet()) {
            String oldKey = entry.getKey();
            String newKey = entry.getValue();

            if (fileConfig.contains(oldKey)) {
                if (!fileConfig.contains(newKey)) {
                    fileConfig.set(newKey, fileConfig.get(oldKey));
                    anyMigrated = true;
                }
                fileConfig.remove(oldKey);
            }
        }

        Config defaultConfig = Config.inMemory();
        serializer.serializeFields(defaultObj, defaultConfig);

        defaultConfig.putAll(fileConfig);

        fileConfig.clear();
        fileConfig.putAll(defaultConfig);
        return anyMigrated || !renameMap.isEmpty();
    }
}
