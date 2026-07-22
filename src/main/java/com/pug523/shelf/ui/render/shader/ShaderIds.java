package com.pug523.shelf.ui.render.shader;

import com.pug523.shelf.common.compat.IdentifierCompat;

import net.minecraft.resources.Identifier;

public class ShaderIds {
    private ShaderIds() {
    }

    public static final String SDF_PATH = shaderPath("sdf");

    public static final Identifier SDF = IdentifierCompat.ofShelf(SDF_PATH);

    public static String shaderPath(String s) {
        //#if MC >= 12104
        return "core/" + s;
        //#elseif MC >= 12102
        //$$ return "core/" + s + "_1.21.3";
        //#elseif MC >= 12100
        //$$ return s + "_1.21.1";
        //#elseif MC >= 11802
        //$$ return s + "_1.20.6";
        //#else
        //$$ return s + "_1.17.1";
        //#endif
    }
}
