package com.pug523.shelf.gui.renderer.state;

import com.pug523.shelf.compat.Matrix3x2fCompat;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pug523.shelf.compat.GuiCompat;

//#if MC >= 12104
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
//#endif

//#if MC >= 11900
import net.minecraft.client.gui.navigation.ScreenRectangle;
//#endif

public class VanillaRectangleRenderState implements ShelfGuiElementRenderState {
    public final int x0;
    public final int x1;
    public final int y0;
    public final int y1;

    public final int x0y0Color;
    public final int x0y1Color;
    public final int x1y0Color;
    public final int x1y1Color;

    private final Matrix3x2fCompat pose;

    //#if MC >= 11900
    @Nullable
    private ScreenRectangle bounds;
    @Nullable
    private ScreenRectangle scissorArea;
    //#endif

    public VanillaRectangleRenderState(GuiCompat gui, int x0, int x1, int y0, int y1, int x0y0Color, int x0y1Color,
                                       int x1y0Color, int x1y1Color) {
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;

        this.x0y0Color = x0y0Color;
        this.x0y1Color = x0y1Color;
        this.x1y0Color = x1y0Color;
        this.x1y1Color = x1y1Color;

        this.pose = Matrix3x2fCompat.copy(gui.getPoseStack());
    }

    //#if MC >= 11900
    public void setRectangles(GuiCompat gui, @Nullable ScreenRectangle scissorArea) {
        this.setRectangles(scissorArea, RenderStateUtil.bounds(x0, y0, x1, y1, Matrix3x2fCompat.copy(gui.getPoseStack()), scissorArea));
    }

    public void setRectangles(@Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) {
        this.scissorArea = scissorArea;
        this.bounds = bounds;
    }

    @Override
    public @Nullable ScreenRectangle bounds() {
        return bounds;
    }

    @Override
    public @Nullable ScreenRectangle scissorArea() {
        return scissorArea;
    }
    //#endif

    @Override
    public void buildVertices(@NonNull VertexConsumer vertices) {
        this.writeVertex(vertices, this.x1, this.y0, this.x1y0Color);
        this.writeVertex(vertices, this.x0, this.y0, this.x0y0Color);
        this.writeVertex(vertices, this.x0, this.y1, this.x0y1Color);
        this.writeVertex(vertices, this.x1, this.y1, this.x1y1Color);
    }

    private void writeVertex(@NonNull VertexConsumer vertices, int x, int y, int color) {
        RenderStateUtil.addVertexWith2DPose(vertices, this.pose, x, y).setColor(color);
        //#if MC <= 12006
        //$$ vertices.endVertex();
        //#endif
    }

    //#if MC >= 12104
    @Override
    public @NonNull RenderPipeline pipeline() {
        return RenderPipelines.GUI;
    }
    //#endif
}
