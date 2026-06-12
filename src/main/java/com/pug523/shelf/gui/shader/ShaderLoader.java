package com.pug523.shelf.gui.shader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.pug523.shelf.Shelf;

public class ShaderLoader {
    private static boolean initialized = false;

    private static final String SDF_VSH_PATH = "/assets/shelf/shaders/core/sdf.vsh";
    private static final String SDF_FSH_PATH = "/assets/shelf/shaders/core/sdf.fsh";

    public static void tryInitialize() {
        if (initialized)
            return;

        try {
            String vshContent = loadResourceToString(SDF_VSH_PATH);
            String fshContent = loadResourceToString(SDF_FSH_PATH);

            ShaderManager.loadSdfShader(vshContent, fshContent);
            initialized = true;
        } catch (Exception e) {
            Shelf.LOGGER.error("Failed to initialize shader: {}", e.getMessage());
        }
    }

    private static String loadResourceToString(String path) throws Exception {
        try (InputStream is = ShaderLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new java.io.FileNotFoundException("Shader file not found in Jar: " + path);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
    }
}
