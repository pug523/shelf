package com.pug523.shelf.gui.renderer;

import com.pug523.shelf.gui.shader.ShaderManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

public class SdfRenderBatch {
    private static final int MAX_QUADS = 512;
    private static final int VERTICES_PER_QUAD = 4;
    private static final int VERTEX_SIZE = 8; // x, y, u, v, r, g, b, a

    private final FloatBuffer buffer;
    private int vao = -1;
    private int vbo = -1;
    private int ebo = -1;
    private boolean initialized = false;

    private int quadCount = 0;
    private float currentWidth = -1;
    private float currentHeight = -1;
    private float currentRadius = -1;

    public SdfRenderBatch() {
        // Constructor now ONLY allocates client-side memory.
        // It does NOT touch any global OpenGL bind states, making class loading 100% safe.
        int maxVertices = MAX_QUADS * VERTICES_PER_QUAD;
        this.buffer = BufferUtils.createFloatBuffer(maxVertices * VERTEX_SIZE);
    }

    private void ensureInitialized() {
        if (initialized)
            return;

        int maxVertices = MAX_QUADS * VERTICES_PER_QUAD;
        long totalBufferSize = (long) maxVertices * VERTEX_SIZE * Float.BYTES;

        this.vao = GL30.glGenVertexArrays();
        this.vbo = GL15.glGenBuffers();
        this.ebo = GL15.glGenBuffers();

        // Backup current vanilla states to prevent any context leakage during first initialization
        int[] originalVao = new int[1];
        int[] originalVbo = new int[1];
        GL30.glGetIntegerv(GL30.GL_VERTEX_ARRAY_BINDING, originalVao);
        GL30.glGetIntegerv(GL15.GL_ARRAY_BUFFER_BINDING, originalVbo);

        // Setup our buffer layouts securely
        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, totalBufferSize, GL15.GL_DYNAMIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        int[] indices = new int[MAX_QUADS * 6];
        for (int i = 0; i < MAX_QUADS; i++) {
            int offset = i * 4;
            indices[i * 6] = offset;
            indices[i * 6 + 1] = offset + 1;
            indices[i * 6 + 2] = offset + 2;
            indices[i * 6 + 3] = offset + 2;
            indices[i * 6 + 4] = offset + 3;
            indices[i * 6 + 5] = offset;
        }
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

        // PERFECT RESTORATION: Restore vanilla states exactly as they were before we intervened
        GL30.glBindVertexArray(originalVao[0]);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, originalVbo[0]);

        this.initialized = true;
    }

    public void queueQuad(float x, float y, float width, float height, float radius, float r, float g, float b,
            float a) {
        // If parameters change, flush the existing queue first to update uniforms safely.
        if (quadCount >= MAX_QUADS || (currentWidth != width || currentHeight != height || currentRadius != radius)) {
            if (quadCount > 0) {
                flushImmediate();
            }
            this.currentWidth = width;
            this.currentHeight = height;
            this.currentRadius = radius;
        }

        // Top left
        buffer.put(x).put(y).put(0.0f).put(0.0f).put(r).put(g).put(b).put(a);
        // Bottom left
        buffer.put(x).put(y + height).put(0.0f).put(1.0f).put(r).put(g).put(b).put(a);
        // Bottom right
        buffer.put(x + width).put(y + height).put(1.0f).put(1.0f).put(r).put(g).put(b).put(a);
        // Top right
        buffer.put(x + width).put(y).put(1.0f).put(0.0f).put(r).put(g).put(b).put(a);

        quadCount++;
    }

    public void flushImmediate() {
        if (quadCount == 0) {
            return;
        }

        ensureInitialized();

        ShaderManager.bind();
        ShaderManager.setUniform2f(currentWidth, currentHeight);
        ShaderManager.setUniform1f(currentRadius);

        buffer.flip();

        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

        int[] actualSize = new int[1];
        GL15.glGetBufferParameteriv(GL15.GL_ARRAY_BUFFER, GL15.GL_BUFFER_SIZE, actualSize);
        int maxVertices = MAX_QUADS * VERTICES_PER_QUAD;
        long totalBufferSize = (long) maxVertices * VERTEX_SIZE * Float.BYTES;

        if (actualSize[0] < totalBufferSize) {
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, totalBufferSize, GL15.GL_DYNAMIC_DRAW);
        }

        int stride = VERTEX_SIZE * Float.BYTES;
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, stride, 0);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, stride, 2 * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(2, 4, GL11.GL_FLOAT, false, stride, 4 * Float.BYTES);
        GL20.glEnableVertexAttribArray(2);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);

        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
        GL11.glDrawElements(GL11.GL_TRIANGLES, quadCount * 6, GL11.GL_UNSIGNED_INT, 0);

        GL30.glBindVertexArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        buffer.clear();
        quadCount = 0;
    }
}
