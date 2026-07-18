package com.pug523.shelf.gui.renderer.state;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.compat.Matrix3x2fCompat;
import com.pug523.shelf.gui.renderer.RenderPipelines;
import com.pug523.shelf.gui.renderer.shader.UniformApplier;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

//#if MC >= 12106
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.pug523.shelf.gui.renderer.SdfParamBufferPool;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryStack;
//#endif

//#if MC >= 12104
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
//#else
//$$ import com.mojang.blaze3d.shaders.Uniform;
//#endif

//#if MC >= 11900
import net.minecraft.client.gui.navigation.ScreenRectangle;
//#endif

public class SdfRenderState implements ShelfGuiElementRenderState, UniformApplier {
    //#if MC >= 12106
    private final GpuBufferSlice sdfParamsBufferSlice;
    //#endif

    public final Matrix3x2fCompat pose;
    public final float x0;
    public final float y0;
    public final float x1;
    public final float y1;
    public final float width;
    public final float height;
    public final float radius;

    private final float u0;
    private final float v0;
    private final float u1;
    private final float v1;

    public final int x0y0Color;
    public final int x0y1Color;
    public final int x1y0Color;
    public final int x1y1Color;

    //#if MC >= 11900
    private @Nullable ScreenRectangle scissorArea;
    private @Nullable ScreenRectangle bounds;
    //#endif

    public SdfRenderState(GuiCompat gui, float x0, float y0, float x1, float y1,
                          float width, float height, float radius,
                          int x0y0Color, int x0y1Color, int x1y0Color, int x1y1Color) {
        this(gui, x0, y0, x1, y1, width, height, radius, 0.0f, 0.0f, 1.0f, 1.0f, x0y0Color, x0y1Color, x1y0Color, x1y1Color);
    }

    public SdfRenderState(GuiCompat gui, float x0, float y0, float x1, float y1,
                          float width, float height, float radius,
                          float u0, float v0, float u1, float v1,
                          int x0y0Color, int x0y1Color, int x1y0Color, int x1y1Color) {
        this.pose = Matrix3x2fCompat.copy(gui.getPoseStack());
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.width = width;
        this.height = height;
        this.radius = radius;
        this.u0 = u0;
        this.v0 = v0;
        this.u1 = u1;
        this.v1 = v1;

        this.x0y0Color = x0y0Color;
        this.x0y1Color = x0y1Color;
        this.x1y0Color = x1y0Color;
        this.x1y1Color = x1y1Color;

        //#if MC >= 12106
        this.sdfParamsBufferSlice = SdfParamBufferPool.allocate();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer byteBuffer = Std140Builder.onStack(stack, SdfParamBufferPool.ELEMENT_SIZE)
                .putVec4(this.width, this.height, this.radius, 0.0f).get();
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.sdfParamsBufferSlice, byteBuffer);
        }
        //#endif
    }

    //#if MC >= 11900
    public void setRectangles(GuiCompat gui, @Nullable ScreenRectangle scissorArea) {
        this.setRectangles(scissorArea, RenderStateUtil.bounds((int) x0, (int) y0, (int) x1, (int) y1, Matrix3x2fCompat.copy(gui.getPoseStack()), scissorArea));
    }

    public void setRectangles(@Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) {
        this.scissorArea = scissorArea;
        this.bounds = bounds;
    }

    @Override
    public @Nullable ScreenRectangle scissorArea() {
        return this.scissorArea;
    }

    @Override
    public @Nullable ScreenRectangle bounds() {
        return this.bounds;
    }
    //#endif

    //#if MC >= 12104
    @Override
    public @NonNull RenderPipeline pipeline() {
        return RenderPipelines.SDF_PIPELINE;
    }
    //#endif

    @Override
    public void buildVertices(@NonNull VertexConsumer vertices) {
        writeVertex(vertices, x0, y0, u0, v0, this.x0y0Color);
        writeVertex(vertices, x0, y1, u0, v1, this.x0y1Color);
        writeVertex(vertices, x1, y1, u1, v1, this.x1y1Color);
        writeVertex(vertices, x1, y0, u1, v0, this.x1y0Color);
    }

    private void writeVertex(@NonNull VertexConsumer vertices, float x, float y, float u, float v, int cornerColor) {
        RenderStateUtil.addVertexWith2DPose(vertices, this.pose, x, y).setUv(u, v).setColor(cornerColor);
    }

    //#if MC >= 12104
    @Override
    public void applyUniforms(RenderPass renderPass) {
        //#if MC >= 12106
        renderPass.setUniform(RenderPipelines.SDF_PARAMS_UBO_NAME, this.sdfParamsBufferSlice);
        //#else
        //$$ renderPass.setUniform(RenderPipelines.SDF_PARAMS_UNIFORM_NAME, this.width, this.height, this.radius, 0.0f);
        //#endif
    }
    //#else
    //$$ @Override
    //$$ public void applyUniforms(Uniform uniform) {
    //$$     uniform.set(this.width, this.height, this.radius, 0.0f);
    //$$     // uniform.upload();
    //$$ }
    //#endif
}
