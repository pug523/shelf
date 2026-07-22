package com.pug523.shelf.ui.render.state;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pug523.shelf.common.compat.GuiCompat;
import com.pug523.shelf.common.compat.Matrix3x2fCompat;
import com.pug523.shelf.ui.render.RenderPipelines;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

//#if MC >= 12104
import com.mojang.blaze3d.pipeline.RenderPipeline;
//#endif

//#if MC >= 11900
import net.minecraft.client.gui.navigation.ScreenRectangle;
//#endif

public class SdfRenderState implements ShelfGuiElementRenderState {
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
        writeVertex(vertices, this.x0, this.y0, this.u0, this.v0, this.x0y0Color, this.radius);
        writeVertex(vertices, this.x0, this.y1, this.u0, this.v1, this.x0y1Color, this.radius);
        writeVertex(vertices, this.x1, this.y1, this.u1, this.v1, this.x1y1Color, this.radius);
        writeVertex(vertices, this.x1, this.y0, this.u1, this.v0, this.x1y0Color, this.radius);
    }

    private void writeVertex(@NonNull VertexConsumer vertices, float x, float y, float u, float v, int cornerColor, float cornerRadius) {
        int cornerRadiusPackedBits = Float.floatToIntBits(cornerRadius);
        int cornerRadiusU = cornerRadiusPackedBits & 0xFFFF;
        int cornerRadiusV = (cornerRadiusPackedBits >> 16) & 0xFFFF;

        //#if MC >= 12100
        RenderStateUtil.addVertexWith2DPose(vertices, this.pose, x, y)
            .setColor(cornerColor)
            .setUv(u, v)
            .setUv1(cornerRadiusU, cornerRadiusV)
            .setUv2((int) this.width, (int) this.height);
        //#else
        //$$ RenderStateUtil.addVertexWith2DPose(vertices, this.pose, x, y);
        //$$ RenderStateUtil.color(vertices, cornerColor);
        //$$ vertices.uv(u, v);
        //$$ vertices.overlayCoords(cornerRadiusU, cornerRadiusV);  // sets uv1
        //$$ vertices.uv2((int) this.width, (int) this.height);
        //$$ // vertices.normal(0, 0, 0);
        //$$ vertices.endVertex();
        //#endif
    }
}
