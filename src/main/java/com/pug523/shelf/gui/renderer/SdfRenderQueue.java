package com.pug523.shelf.gui.renderer;

import java.util.ArrayList;
import java.util.List;

public class SdfRenderQueue {
    private static final List<SdfQuadTask> QUEUE = new ArrayList<>();
    private static final float PADDING = 1.5f;
    private static boolean isBuffering = false;

    public static void startBuffering() {
        QUEUE.clear();
        isBuffering = true;
    }

    public static void queueSdfQuad(float x, float y, float w, float h, float radius, int color, boolean addPadding) {
        if (addPadding) {
            x -= PADDING;
            y -= PADDING;
            w += PADDING * 2;
            h += PADDING * 2;
        }

        if (!isBuffering) {
            // Fallback for older versions or immediate rendering contexts
            RenderUtil.beginRender();
            RenderUtil.drawSdfQuadImmediate(x, y, w, h, radius, color);
            RenderUtil.endRender();
            return;
        }
        QUEUE.add(new SdfQuadTask(x, y, w, h, radius, color));
    }

    public static void flushAll() {
        if (QUEUE.isEmpty()) {
            isBuffering = false;
            return;
        }

        // Setup OpenGL states once for all buffered quads
        RenderUtil.beginRender();

        for (SdfQuadTask task : QUEUE) {
            // Update uniforms and draw without breaking the core shader bindings
            RenderUtil.drawSdfQuadImmediate(task.x, task.y, task.w, task.h, task.radius, task.color);
        }

        // RenderUtil.drawDebugRawQuad(50.0f, 50.0f, 100.0f, 100.0f);
        RenderUtil.endRender();
        QUEUE.clear();
        isBuffering = false;
    }

    private static class SdfQuadTask {
        final float x, y, w, h, radius;
        final int color;

        SdfQuadTask(float x, float y, float w, float h, float radius, int color) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.radius = radius;
            this.color = color;
        }
    }
}
