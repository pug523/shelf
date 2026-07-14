package com.pug523.shelf.gui.renderer.shader;

//#if MC <= 12105
//$$ import net.minecraft.client.renderer.RenderType;
//$$ import com.pug523.shelf.gui.renderer.RenderPipelines;
//$$ import com.pug523.shelf.gui.renderer.state.SdfRenderState;
//$$ import java.util.HashMap;
//$$ import java.util.Map;
//#endif

public class SdfRenderTypeCache {
    //#if MC <= 12105
    //$$ private static final Map<SdfRenderState, RenderType> CACHE = new HashMap<>();

    //$$ public static RenderType get(RenderType baseGuiType, SdfRenderState state) {
    //$$     // Clear cache periodically if it grows too large, or implement a proper cleanup
    //$$     if (CACHE.size() > 1000) CACHE.clear();

    //$$     return CACHE.computeIfAbsent(state, s -> new SdfRenderType("gui_sdf_" + s.hashCode(), baseGuiType, s));
    //$$ }
    //#endif
}
