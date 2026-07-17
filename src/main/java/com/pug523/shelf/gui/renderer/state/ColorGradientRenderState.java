package com.pug523.shelf.gui.renderer.state;

import com.pug523.shelf.compat.Matrix3x2fCompat;
import com.pug523.shelf.gui.renderer.RenderUtil;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pug523.shelf.compat.GuiCompat;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;

//#if MC >= 12104
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
//#endif

public class ColorGradientRenderState implements ShelfGuiElementRenderState {
    private final Matrix3x2fCompat pose;
    @Nullable
    private final ScreenRectangle bounds;
    @Nullable
    private final ScreenRectangle scissorArea;

    public final int x0;
    public final int x1;
    public final int y0;
    public final int y1;

    public final int x0y0Color;
    public final int x0y1Color;
    public final int x1y0Color;
    public final int x1y1Color;

    public ColorGradientRenderState(GuiCompat gui, int x0, int x1, int y0, int y1, int x0y0Color, int x0y1Color,
                                    int x1y0Color, int x1y1Color) {
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;

        this.x0y0Color = x0y0Color;
        this.x0y1Color = x0y1Color;
        this.x1y0Color = x1y0Color;
        this.x1y1Color = x1y1Color;

        this.scissorArea = gui.peekScissorStack();
        this.pose = Matrix3x2fCompat.copy(gui.getPoseStack());
        this.bounds = RenderStateUtil.bounds(this.x0, this.y0, this.x1, this.y1, this.pose, this.scissorArea);
    }

    @Override
    public void buildVertices(@NonNull VertexConsumer vertices) {
        RenderStateUtil.addVertexWith2DPose(vertices, this.pose, this.x1, this.y0).setColor(this.x1y0Color);
        RenderStateUtil.addVertexWith2DPose(vertices, this.pose, this.x0, this.y0).setColor(this.x0y0Color);
        RenderStateUtil.addVertexWith2DPose(vertices, this.pose, this.x0, this.y1).setColor(this.x0y1Color);
        RenderStateUtil.addVertexWith2DPose(vertices, this.pose, this.x1, this.y1).setColor(this.x1y1Color);
    }

    @Override
    public @Nullable ScreenRectangle bounds() {
        return bounds;
    }

    //#if MC >= 12104
    @Override
    public @NonNull RenderPipeline pipeline() {
        return RenderPipelines.GUI;
    }
    //#endif

    @Override
    public @Nullable ScreenRectangle scissorArea() {
        return scissorArea;
    }
}
