package com.pug523.shelf.common.i18n;

import net.fabricmc.loader.api.FabricLoader;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/// Manages language file resolution, caching, and text translation fallback chains.
public class TranslatorEngine {
    private final Map<String, Map<String, String>> loadedDictionaries = new ConcurrentHashMap<>();
    private final List<String> languageFallbackChain = new CopyOnWriteArrayList<>();
    private final Path langDirectory;

    /// Initializes the translation engine.
    ///
    /// @param langDirectory The directory containing language files (e.g., config/mymod/lang/)
    public TranslatorEngine(@NonNull Path langDirectory) {
        this.langDirectory = langDirectory;
        // Default state
        this.languageFallbackChain.add("en_us");
    }

    /// Creates a translator instance from a specific Fabric mod's asset directory.
    ///
    /// @param modId The unique identifier of the target mod
    /// @return A new TranslatorEngine instance, or null if the mod or its lang directory missing
    @Nullable
    public static TranslatorEngine fromModId(String modId) {
        return FabricLoader.getInstance().getModContainer(modId)
            .flatMap(container -> container.findPath("assets/" + modId + "/lang"))
            .map(TranslatorEngine::new)
            .orElse(null);
    }

    /// Sets the active language fallback chain.
    /// Order determines priority (e.g., ["ja_jp", "en_uk", "en_us"]).
    ///
    /// @param languages The list of language codes to fall back through
    public void setLanguageChain(List<String> languages) {
        this.languageFallbackChain.clear();
        this.languageFallbackChain.addAll(languages);

        // Ensure all languages in the new chain are loaded
        for (String lang : languages) {
            loadLanguageIfNeeded(lang);
        }
    }

    /// Translates and formats a key based on the current language chain.
    ///
    /// @param key  The translation key (e.g., "item.mod.magic_sword")
    /// @param args Format arguments for {} placeholders
    /// @return The translated and formatted string, or the raw key if not found
    public String translate(String key, Object... args) {
        for (String langCode : languageFallbackChain) {
            Map<String, String> dictionary = loadedDictionaries.get(langCode);

            if (dictionary != null && dictionary.containsKey(key)) {
                String template = dictionary.get(key);
                return Formatter.format(template, args);
            }
        }
        // Fallback: return the unformatted key if missing completely.
        return key;
    }

    /// Clears the dictionary cache. Call this on resource reload.
    public void clearCache() {
        this.loadedDictionaries.clear();
    }

    private void loadLanguageIfNeeded(String langCode) {
        if (loadedDictionaries.containsKey(langCode)) return;

        // Try JSON first, then TOML.
        Path jsonPath = langDirectory.resolve(langCode + ".json");
        Path tomlPath = langDirectory.resolve(langCode + ".toml");

        Map<String, String> dictionary = Collections.emptyMap();
        if (jsonPath.toFile().exists()) {
            dictionary = DictionaryLoader.loadDictionary(jsonPath);
        } else if (tomlPath.toFile().exists()) {
            dictionary = DictionaryLoader.loadDictionary(tomlPath);
        }

        loadedDictionaries.put(langCode, dictionary);
    }
}
