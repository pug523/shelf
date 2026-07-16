package com.pug523.shelf.gui.renderer;

import com.pug523.shelf.compat.IdentifierCompat;

import net.minecraft.resources.Identifier;

public class ShaderIds {
    private ShaderIds() {
    }

    public static final Identifier SDF = IdentifierCompat.ofShelf(shaderPath("sdf"));

    private static String shaderPath(String s) {
        //#if MC >= 12102
        return "core/" + s;
        //#else
        //$$ return s;
        //#endif
    }
}
