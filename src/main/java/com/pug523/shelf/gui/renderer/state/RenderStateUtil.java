package com.pug523.shelf.gui.renderer.state;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.gui.navigation.ScreenRectangle;

public class RenderStateUtil {
    public static @Nullable ScreenRectangle bounds(final int x0, final int y0, final int x1, final int y1,
            final Matrix3x2fc pose, final @Nullable ScreenRectangle scissorArea) {
        Matrix3x2f m = new Matrix3x2f(pose);
        ScreenRectangle bounds = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(m);
        return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
    }

    public static @NonNull VertexConsumer addVertexWith2DPose(@NonNull VertexConsumer vertices, Matrix3x2fc pose,
            float x, float y) {
        Vector2f pos = pose.transformPosition(x, y, new Vector2f());
        return vertices.addVertex(pos.x(), pos.y(), 0.0f);
    }
}
