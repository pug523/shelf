package com.pug523.shelf.gui.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class GlStateManager {
    private int program;
    private int vertexArray;
    private int arrayBuffer;
    private int elementArrayBuffer;
    private boolean blend;
    private boolean depthTest;
    private boolean scissorTest;

    public void capture() {
        this.program = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        this.vertexArray = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        this.arrayBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
        this.elementArrayBuffer = GL11.glGetInteger(GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING);
        this.blend = GL11.glIsEnabled(GL11.GL_BLEND);
        this.depthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        this.scissorTest = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
    }

    public void restore() {
        GL20.glUseProgram(program);
        GL30.glBindVertexArray(vertexArray);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, arrayBuffer);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementArrayBuffer);

        if (blend) {
            GL11.glEnable(GL11.GL_BLEND);
        } else {
            GL11.glDisable(GL11.GL_BLEND);
        }

        if (depthTest) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        } else {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }

        if (scissorTest) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
        } else {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }
}
