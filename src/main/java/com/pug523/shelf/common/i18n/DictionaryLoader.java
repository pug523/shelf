package com.pug523.shelf.common.i18n;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class DictionaryLoader {
    private DictionaryLoader() {
    }

    /// Loads and flattens a JSON or TOML file into a key-value dictionary.
    ///
    /// @param filePath Path to the .json or .toml file
    /// @return An immutable map of flattened translation keys to values
    public static Map<String, String> loadDictionary(Path filePath) {
        if (!filePath.toFile().exists()) {
            return Collections.emptyMap();
        }

        Map<String, String> flatMap = new HashMap<>();
        try (FileConfig config = FileConfig.builder(filePath).build()) {
            config.load();
            flattenConfig(config, "", flatMap);
        }

        return Map.copyOf(flatMap); // Return immutable map for thread-safe reads
    }

    private static void flattenConfig(Config config, String prefix, Map<String, String> map) {
        for (Config.Entry entry : config.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Config nestedConfig) {
                flattenConfig(nestedConfig, key, map);
            } else if (value != null) {
                map.put(key, String.valueOf(value));
            }
        }
    }
}
