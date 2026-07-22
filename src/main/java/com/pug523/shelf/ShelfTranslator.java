package com.pug523.shelf;

import com.pug523.shelf.common.i18n.TranslatorEngine;
import org.jspecify.annotations.Nullable;

public class ShelfTranslator {
    @Nullable
    private static TranslatorEngine engine = null;

    public static void init() {
        engine = TranslatorEngine.fromModId(Shelf.MOD_ID);
        if (engine != null) {
            engine.setLanguageChain(ShelfConfigManager.getConfig().layoutConfig.languages);
        }
    }

    public static TranslatorEngine engine() {
        if (engine == null) {
            init();
        }
        return engine;
    }
}
