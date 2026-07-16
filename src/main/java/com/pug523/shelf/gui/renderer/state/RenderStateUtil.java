package com.pug523.shelf.gui.renderer.state;

import com.pug523.shelf.compat.Matrix3x2fCompat;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.gui.navigation.ScreenRectangle;

//#if MC >= 12106
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;
//#else
//$$ import org.joml.Matrix4f;
//$$ import org.joml.Vector4f;
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

public class RenderStateUtil {
    public static @Nullable ScreenRectangle bounds(final int x0, final int y0, final int x1, final int y1,
                                                   final Matrix3x2fCompat pose, final @Nullable ScreenRectangle scissorArea) {
        //#if MC >= 12106
        Matrix3x2fCompat m = pose.copy();
        ScreenRectangle bounds = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(m.pose);
        return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
        //#else
        //$$ Matrix4f matrix = pose.pose.pose();
        //$$ // Transform the top-left and bottom-right corners using the Matrix4f
        //$$ Vector4f topLeft = new Vector4f((float) x0, (float) y0, 0.0f, 1.0f).mul(matrix);
        //$$ Vector4f bottomRight = new Vector4f((float) x1, (float) y1, 0.0f, 1.0f).mul(matrix);
        //$$ int tx0 = Math.round(Math.min(topLeft.x(), bottomRight.x()));
        //$$ int ty0 = Math.round(Math.min(topLeft.y(), bottomRight.y()));
        //$$ int tx1 = Math.round(Math.max(topLeft.x(), bottomRight.x()));
        //$$ int ty1 = Math.round(Math.max(topLeft.y(), bottomRight.y()));
        //$$ ScreenRectangle bounds = new ScreenRectangle(tx0, ty0, tx1 - tx0, ty1 - ty0);
        //$$ return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
        //$$
        //#endif
    }

    public static @NonNull VertexConsumer addVertexWith2DPose(@NonNull VertexConsumer vertices, Matrix3x2fCompat pose,
                                                              float x, float y) {
        //#if MC >= 12106
        Vector2f pos = pose.pose.transformPosition(x, y, new Vector2f());
        return vertices.addVertex(pos.x(), pos.y(), 0.0f);
        //#else
        //$$ Matrix4f matrix = pose.pose.pose();
        //$$ Vector4f pos = new Vector4f(x, y, 0.0f, 1.0f).mul(matrix);
        //$$ return vertices.addVertex(pos.x(), pos.y(), 0.0f);
        //#endif
    }
}
