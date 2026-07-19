package com.pug523.shelf.gui.renderer;

//#if MC >= 12104
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.pug523.shelf.compat.IdentifierCompat;
import com.pug523.shelf.gui.renderer.shader.ShaderIds;
import net.minecraft.resources.Identifier;

//#if MC >= 260200
import com.mojang.blaze3d.PrimitiveTopology;
import net.minecraft.client.renderer.BindGroupLayouts;
//#else
//$$ import com.mojang.blaze3d.shaders.UniformType;
//$$ import com.mojang.blaze3d.vertex.VertexFormat.Mode;
//#endif

//#if MC >= 260000
import com.mojang.blaze3d.pipeline.ColorTargetState;
//#else
//$$ import com.mojang.blaze3d.platform.DepthTestFunction;
//#endif
//#endif

public class RenderPipelines {
    private RenderPipelines() {
    }
    //#if MC >= 12104
    public static final Identifier SDF_PIPELINE_ID = IdentifierCompat.ofShelf("pipeline/sdf");

    // @formatter:off
    public static final RenderPipeline SDF_PIPELINE = RenderPipeline.builder()
            .withLocation(SDF_PIPELINE_ID)
            .withVertexShader(ShaderIds.SDF)
            .withFragmentShader(ShaderIds.SDF)
            //#if MC >= 260200
            .withVertexBinding(0, VertexFormats.POSITION_COLOR_TEX_CORNER_RADIUS_RAW_SIZE)
            .withBindGroupLayout(BindGroupLayouts.GLOBALS)
            .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            //#else
                //$$ .withVertexFormat(VertexFormats.POSITION_COLOR_TEX_CORNER_RADIUS_RAW_SIZE, Mode.QUADS)
                //#if MC >= 12106
                //$$ .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
                //$$ .withUniform("Projection", UniformType.UNIFORM_BUFFER)
                //#else
                //$$ .withUniform("ModelViewMat", UniformType.MATRIX4X4)
                //$$ .withUniform("ProjMat", UniformType.MATRIX4X4)
                //#endif

                //#if MC >= 260000
                //$$ .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                //#else
                //$$ .withBlend(BlendFunction.TRANSLUCENT)
                //$$ .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                //#endif
            //#endif
            .build();
    // @formatter:on
    //#endif
}
