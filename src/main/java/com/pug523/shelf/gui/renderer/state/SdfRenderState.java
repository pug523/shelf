package com.pug523.shelf.gui.renderer.state;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.compat.Matrix3x2fCompat;
import com.pug523.shelf.gui.renderer.RenderPipelines;
import com.pug523.shelf.gui.renderer.shader.UniformApplier;
import net.minecraft.client.gui.navigation.ScreenRectangle;

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
    public final int color;
    private final @Nullable ScreenRectangle scissorArea;
    private final @Nullable ScreenRectangle bounds;

    public SdfRenderState(GuiCompat gui, float x0, float y0,
                          float x1, float y1, float width, float height, float radius, int color,
                          @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) {
        this.pose = Matrix3x2fCompat.copy(gui.getPoseStack());
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.width = width;
        this.height = height;
        this.radius = radius;
        this.color = color;
        this.scissorArea = scissorArea;
        this.bounds = bounds;

        //#if MC >= 12106
        this.sdfParamsBufferSlice = SdfParamBufferPool.allocate();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer byteBuffer = Std140Builder.onStack(stack, SdfParamBufferPool.ELEMENT_SIZE)
                .putVec4(this.width, this.height, this.radius, 0.0f).get();
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.sdfParamsBufferSlice, byteBuffer);
        }
        //#endif
    }

    public SdfRenderState(GuiCompat gui, float x0, float y0,
                          float x1, float y1, float width, float height, float radius, int color,
                          @Nullable ScreenRectangle scissorArea) {

        this(gui, x0, y0, x1, y1, width, height, radius, color, scissorArea,
            RenderStateUtil.bounds((int) x0, (int) y0, (int) x1, (int) y1, Matrix3x2fCompat.copy(gui.getPoseStack()), scissorArea));
    }

    //#if MC >= 12104
    @Override
    public @NonNull RenderPipeline pipeline() {
        return RenderPipelines.SDF_PIPELINE;
    }
    //#endif

    @Override
    public @Nullable ScreenRectangle scissorArea() {
        return this.scissorArea;
    }

    @Override
    public @Nullable ScreenRectangle bounds() {
        return this.bounds;
    }

    @Override
    public void buildVertices(@NonNull VertexConsumer vertices) {
        writeVertex(vertices, x0, y0, 0.0f, 0.0f);
        writeVertex(vertices, x0, y1, 0.0f, 1.0f);
        writeVertex(vertices, x1, y1, 1.0f, 1.0f);
        writeVertex(vertices, x1, y0, 1.0f, 0.0f);
    }

    private void writeVertex(@NonNull VertexConsumer vertices, float x, float y, float u, float v) {
        RenderStateUtil.addVertexWith2DPose(vertices, this.pose, x, y).setUv(u, v).setColor(this.color);
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
    //$$ public void applyUniforms(Uniform sdfParams) {
    //$$     sdfParams.set(this.width, this.height, this.radius, 0.0f);
    //$$ }
    //#endif
}
