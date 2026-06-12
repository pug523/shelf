package com.pug523.shelf.gui.renderer;

import com.mojang.blaze3d.platform.Window;
import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.gui.shader.ShaderLoader;
import com.pug523.shelf.gui.shader.ShaderManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

public class RenderUtil {
    private static final GlStateManager stateManager = new GlStateManager();
    private static final SdfRenderBatch batch = new SdfRenderBatch();
    private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public static void beginRender() {
        ShaderLoader.tryInitialize();
        if (ShaderManager.getProgramId() == -1) {
            return;
        }

        // Context Switch: Guard vanilla states securely.
        stateManager.capture();

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);

        // Ensure texture unit 0 is unbound so it doesn't conflict with custom shader attributes.
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        ShaderManager.bind();

        // Build the projection matrix manually to completely bypass RenderSystem.
        setupOrthographicProjection();
    }

    private static void setupOrthographicProjection() {
        Minecraft mc = Minecraft.getInstance();

        //#if MC >= 11500
        Window window = mc.getWindow();
        //#else
        //$$ Window window = mc.window;
        //#endif

        float width = (float) window.getGuiScaledWidth();
        float height = (float) window.getGuiScaledHeight();

        matrixBuffer.clear();

        // Standard 2D Orthographic Matrix (Near: -1.0, Far: 1.0)
        matrixBuffer.put(0, 2.0f / width);
        matrixBuffer.put(1, 0.0f);
        matrixBuffer.put(2, 0.0f);
        matrixBuffer.put(3, 0.0f);

        matrixBuffer.put(4, 0.0f);
        matrixBuffer.put(5, -2.0f / height); // Inverted Y-axis
        matrixBuffer.put(6, 0.0f);
        matrixBuffer.put(7, 0.0f);

        matrixBuffer.put(8, 0.0f);
        matrixBuffer.put(9, 0.0f);
        matrixBuffer.put(10, -1.0f);
        matrixBuffer.put(11, 0.0f);

        matrixBuffer.put(12, -1.0f);
        matrixBuffer.put(13, 1.0f);
        matrixBuffer.put(14, 0.0f);
        matrixBuffer.put(15, 1.0f);

        matrixBuffer.rewind();
        ShaderManager.setUniformMatrix4(matrixBuffer);
    }

    public static void drawSdfQuadImmediate(float x, float y, float width, float height, float radius, int color) {
        if (ShaderManager.getProgramId() == -1) {
            return;
        }

        float a = ((color >> 24) & 0xFF) / 255.0f;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        batch.queueQuad(x, y, width, height, radius, r, g, b, a);
    }

    public static void drawCircle(GuiCompat gui, int centerX, int centerY, int radius, int color) {
        float diameter = radius * 2f;
        SdfRenderQueue.queueSdfQuad(centerX - radius, centerY - radius, diameter, diameter, radius, color, true);
    }

    public static void drawCapsule(GuiCompat gui, int x, int y, int width, int height, int color) {
        float radius = height / 2.0f;
        SdfRenderQueue.queueSdfQuad(x, y, width, height, radius, color, true);
    }

    public static void drawRoundedRect(GuiCompat gui, int x, int y, int width, int height, float radius, int color) {
        SdfRenderQueue.queueSdfQuad(x, y, width, height, radius, color, true);
    }

    public static void endRender() {
        if (ShaderManager.getProgramId() == -1) {
            return;
        }

        // Execute remaining elements in the batch stream
        batch.flushImmediate();
        ShaderManager.unbind();

        // Restore context state perfectly
        stateManager.restore();
    }

    public static void renderDownwardArrow(GuiCompat gui, int startX, int startY, int color) {
        gui.fill(startX, startY, startX + 5, startY + 1, color);
        gui.fill(startX + 1, startY + 1, startX + 4, startY + 2, color);
        gui.fill(startX + 2, startY + 2, startX + 3, startY + 3, color);
    }

    public static void renderRightwardArrow(GuiCompat gui, int startX, int startY, int color) {
        gui.fill(startX, startY, startX + 1, startY + 5, color);
        gui.fill(startX + 1, startY + 1, startX + 2, startY + 4, color);
        gui.fill(startX + 2, startY + 2, startX + 3, startY + 3, color);
    }

    public static void drawDebugRawQuad(float x, float y, float width, float height) {
        if (ShaderManager.getProgramId() == -1) {
            return;
        }

        ShaderManager.bind();

        ShaderManager.setUniform2f(width, height);
        ShaderManager.setUniform1f(0.0f);

        int debugVao = GL30.glGenVertexArrays();
        int debugVbo = GL15.glGenBuffers();

        GL30.glBindVertexArray(debugVao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, debugVbo);

        // Using neon green (0.0, 1.0, 0.0, 1.0).
        float[] vertices = { x, y, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, // Top Left
                x, y + height, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, // Bottom Left
                x + width, y + height, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, // Bottom Right
                x + width, y, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f // Top Right
        };

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STREAM_DRAW);

        int stride = 8 * Float.BYTES;
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, stride, 0);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, stride, 2 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(2, 4, GL11.GL_FLOAT, false, stride, 4 * Float.BYTES);
        GL20.glEnableVertexAttribArray(2);

        GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, 4);

        GL30.glBindVertexArray(0);
        GL15.glDeleteBuffers(debugVbo);
        GL30.glDeleteVertexArrays(debugVao);
    }
}
