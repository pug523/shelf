package com.pug523.shelf.ui.render.shader;

// @formatter:off
//#if MC <= 12105
//$$ import com.pug523.shelf.ui.render.VertexFormats;
//$$ import com.pug523.shelf.ui.render.RenderTypes;
//$$ import com.mojang.blaze3d.vertex.VertexFormat;
//$$ import net.minecraft.client.renderer.*;
    //#if MC >= 12104
    //$$ import com.mojang.blaze3d.buffers.BufferType;
    //$$ import com.mojang.blaze3d.buffers.BufferUsage;
    //$$ import com.mojang.blaze3d.vertex.PoseStack;
    //#else
    //$$ import com.pug523.shelf.ui.render.shader.ShaderIds;
    //$$ import net.minecraft.client.Minecraft;
    //#endif
//#endif
// @formatter:on

//#if 11800 <= MC && MC <= 12101
//$$ import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
//#endif

//#if MC <= 11701
//$$ import net.minecraft.server.packs.resources.ResourceManager;
//$$ import java.io.IOException;
//#endif

public class ShaderManager {
    //#if MC <= 12103
    //$$ private static CompiledShaderProgram COMPILED_SDF_SHADER = null;
    //$$ public static CompiledShaderProgram compiledSdfShader() {
    //#if 12102 <= MC
    //$$ init();
    //#endif
    //$$     return COMPILED_SDF_SHADER;
    //$$ }
    //#if 12102 <= MC
    //$$ private static boolean initialized = false;
    //$$ private static void init() {
    //$$     if (!initialized) {
    //$$         COMPILED_SDF_SHADER = Minecraft.getInstance().getShaderManager().getProgram(RenderTypes.SDF_SHADER);
    //$$         if (COMPILED_SDF_SHADER != null) {
    //$$             initialized = true;
    //$$         }
    //$$     }
    //$$ }
    //#endif
    //#endif

    public static void registerEvent() {
    //#if 11800 <= MC && MC <= 12101
    //$$     CoreShaderRegistrationCallback.EVENT.register((context) -> {
    //$$         context.register(
    //$$             ShaderIds.SDF,
    //$$             VertexFormats.POSITION_COLOR_TEX_CORNER_RADIUS_RAW_SIZE,
    //$$             (shaderProgram) -> {
    //$$                 COMPILED_SDF_SHADER = shaderProgram;
    //$$             }
    //$$         );
    //$$     });
    //#endif
    }

    //#if MC <= 11701
    //$$ public static void loadSdfShader(ResourceManager resourceManager) {
    //$$     try {
    //$$         COMPILED_SDF_SHADER = new ShaderInstance(resourceManager, ShaderIds.SDF_PATH, VertexFormats.POSITION_COLOR_TEX_CORNER_RADIUS_RAW_SIZE);
    //$$     } catch (IOException e) {
    //$$         e.printStackTrace();
    //$$     }
    //$$ }

    //$$ public static void closeSdfShader() {
    //$$     if (COMPILED_SDF_SHADER != null) {
    //$$         COMPILED_SDF_SHADER.close();
    //$$         COMPILED_SDF_SHADER = null;
    //$$     }
    //$$ }
    //#endif
}
