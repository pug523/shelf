package com.pug523.shelf.gui.renderer;

// @formatter:off
//#if MC <= 12105
//$$ import com.mojang.blaze3d.buffers.BufferType;
//$$ import com.mojang.blaze3d.buffers.BufferUsage;
//$$ import com.mojang.blaze3d.vertex.DefaultVertexFormat;
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//$$ import com.mojang.blaze3d.vertex.VertexFormat;
//$$ import net.minecraft.client.renderer.RenderStateShard;
//$$ import net.minecraft.client.renderer.RenderType;
    //#if MC >= 12104
    //$$ import com.pug523.shelf.gui.renderer.RenderPipelines;
    //#elseif MC >= 12102
    //$$ import net.minecraft.client.renderer.RenderStateShard;
    //#endif
//#endif
// @formatter:on


public class RenderTypes
    //#if 12102 <= MC && MC <= 12103
    //$$ extends RenderStateShard
    //#endif
{
    //#if MC <= 12105
    //#if MC >= 12104
    //$$ public static final RenderType SDF_RENDER_TYPE = RenderType.create(
    //$$     "shelf_sdf",
    //$$     RenderType.SMALL_BUFFER_SIZE,
    //$$     RenderPipelines.SDF_PIPELINE,
    //$$     RenderType.CompositeState.builder().createCompositeState(false)
    //$$ );
    //#elseif MC >= 12102
    //$$ public static final RenderType SDF_RENDER_TYPE = RenderType.create(
    //$$     "shelf_sdf",
    //$$     DefaultVertexFormat.POSITION_TEX_COLOR,
    //$$     VertexFormat.Mode.QUADS,
    //$$     RenderType.SMALL_BUFFER_SIZE,
    //$$     RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard()).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDepthTestState(LEQUAL_DEPTH_TEST).createCompositeState(false)
    //$$ );
    //#else
    //#endif
    //#endif
}
