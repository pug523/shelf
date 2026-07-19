package com.pug523.shelf.gui.renderer;

// @formatter:off
//#if MC <= 12105
//$$ import com.pug523.shelf.gui.renderer.shader.ShaderManager;
//$$ import com.mojang.blaze3d.vertex.VertexFormat;
//$$ import net.minecraft.client.renderer.*;
//#if MC >= 12104
//$$ import com.mojang.blaze3d.buffers.BufferType;
//$$ import com.mojang.blaze3d.buffers.BufferUsage;
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#else
//$$ import com.pug523.shelf.gui.renderer.shader.ShaderIds;
//$$ import net.minecraft.client.Minecraft;
//#endif
//#endif
// @formatter:on

public class RenderTypes {
    //#if MC <= 12105
    //#if MC >= 12104
    //$$ public static final RenderType SDF_RENDER_TYPE = RenderType.create(
    //$$     "shelf_sdf",
    //$$     RenderType.SMALL_BUFFER_SIZE,
    //$$     RenderPipelines.SDF_PIPELINE,
    //$$     RenderType.CompositeState.builder().createCompositeState(false)
    //$$ );
    //#else
    //#if MC >= 12102
    //$$ public static final ShaderProgram SDF_SHADER = new ShaderProgram(ShaderIds.SDF, VertexFormats.POSITION_COLOR_TEX_CORNER_RADIUS_RAW_SIZE, ShaderDefines.EMPTY);
    //#endif

    //$$ public static final RenderType SDF_RENDER_TYPE = RenderType.create(
    //$$     "shelf_sdf",
    //$$     VertexFormats.POSITION_COLOR_TEX_CORNER_RADIUS_RAW_SIZE,
    //$$     VertexFormat.Mode.QUADS,
    //$$     RenderType.SMALL_BUFFER_SIZE,
    //$$     RenderType.CompositeState.builder()
    //#if MC >= 12102
    //$$         .setShaderState(new RenderStateShard.ShaderStateShard(SDF_SHADER))
    //#else
    //$$         .setShaderState(new RenderStateShard.ShaderStateShard(ShaderManager::compiledSdfShader))
    //#endif
    //$$         .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
    //$$         .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
    //$$         .createCompositeState(false)
    //$$ );
    //#endif
    //#endif
}
