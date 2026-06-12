package com.pug523.shelf.gui.shader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import java.nio.FloatBuffer;

public class ShaderManager {
    private static int programId = -1;
    private static int uProjMat;
    private static int uSize;
    private static int uCornerRadius;

    public static void loadSdfShader(String vshSource, String fshSource) {
        if (programId != -1) {
            return;
        }

        int vsh = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vsh, vshSource);
        GL20.glCompileShader(vsh);
        if (GL20.glGetShaderi(vsh, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("VSH Compile Error: " + GL20.glGetShaderInfoLog(vsh));
        }

        int fsh = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fsh, fshSource);
        GL20.glCompileShader(fsh);
        if (GL20.glGetShaderi(fsh, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("FSH Compile Error: " + GL20.glGetShaderInfoLog(fsh));
        }

        programId = GL20.glCreateProgram();
        GL20.glAttachShader(programId, vsh);
        GL20.glAttachShader(programId, fsh);

        // Explicitly bind attribute locations matching the VSH layout.
        GL20.glBindAttribLocation(programId, 0, "Position");
        GL20.glBindAttribLocation(programId, 1, "UV");
        GL20.glBindAttribLocation(programId, 2, "Color");

        GL20.glLinkProgram(programId);
        if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("Shader Link Error: " + GL20.glGetProgramInfoLog(programId));
        }

        uProjMat = GL20.glGetUniformLocation(programId, "ProjMat");
        uSize = GL20.glGetUniformLocation(programId, "Size");
        uCornerRadius = GL20.glGetUniformLocation(programId, "CornerRadius");

        GL20.glDeleteShader(vsh);
        GL20.glDeleteShader(fsh);
    }

    public static void bind() {
        GL20.glUseProgram(programId);
    }

    public static void unbind() {
        GL20.glUseProgram(0);
    }

    public static void setUniformMatrix4(FloatBuffer matrixBuffer) {
        GL20.glUniformMatrix4fv(uProjMat, false, matrixBuffer);
    }

    public static void setUniform2f(float x, float y) {
        GL20.glUniform2f(uSize, x, y);
    }

    public static void setUniform1f(float radius) {
        GL20.glUniform1f(uCornerRadius, radius);
    }

    public static int getProgramId() {
        return programId;
    }
}
