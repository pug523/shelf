package com.pug523.shelf.compat;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

//#if MC >= 12106
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.pipeline.RenderPipeline;
//#else
// @formatter:off
    //$$ import net.minecraft.resources.ResourceLocation;
    //#if MC >= 12102
    //$$ import java.util.function.Function;
    //$$ import net.minecraft.client.renderer.RenderType;
    //#elseif MC >= 11500
    //$$ import com.mojang.blaze3d.systems.RenderSystem;
    //#endif
// @formatter:on
//#endif

//#if MC >= 12000
import net.minecraft.client.gui.GuiGraphicsExtractor;
//#else
//$$ import com.mojang.blaze3d.platform.GlStateManager;
//$$ import net.minecraft.client.Minecraft;
// @formatter:off
    //#if MC >= 11600
    //$$ import com.mojang.blaze3d.vertex.PoseStack;
    //#endif
    //#if MC >= 11500
    //$$ import net.minecraft.client.renderer.MultiBufferSource;
    //#endif
// @formatter:on
//$$ import net.minecraft.client.gui.GuiComponent;
//#endif

public class GuiCompat {
    //#if MC >= 12000
    private final GuiGraphicsExtractor graphics;
    //#elseif MC >= 11600
    //$$ private final PoseStack poseStack;
    //#endif

    //#if MC >= 12000
    public GuiCompat(GuiGraphicsExtractor graphics) {
        this.graphics = graphics;
    }

    public GuiGraphicsExtractor getGraphics() {
        return this.graphics;
    }

    //#elseif MC >= 11600
    //$$ public GuiCompat(PoseStack poseStack) {
    //$$     this.poseStack = poseStack;
    //$$ }
    //$$ public PoseStack getPoseStack() { return this.poseStack; }
    //#else
    //$$ public GuiCompat() {
    //$$ }
    //#endif

    public void fill(int minX, int minY, int maxX, int maxY, int color) {
        //#if MC >= 12000
        this.graphics.fill(minX, minY, maxX, maxY, color);
        //#elseif MC >= 11600
        //$$ GuiComponent.fill(this.poseStack, minX, minY, maxX, maxY, color);
        //#else
        //$$ GuiComponent.fill(minX, minY, maxX, maxY, color);
        //#endif
    }

    public void fillGradient(int x0, int y0, int x1, int y1, int col1, int col2) {
        //#if MC >= 12000
        this.graphics.fillGradient(x0, y0, x1, y1, col1, col2);
        //#elseif MC >= 11600
        //$$ Minecraft.getInstance().gui.fillGradient(this.poseStack, x0, y0, x1, y1, col1, col2);
        //#else
        //$$ Minecraft.getInstance().gui.fillGradient(x0, y0, x1, y1, col1, col2);
        //#endif
    }

    public void text(Font font, Component text, int x, int y, int color, boolean shadow) {
        //#if MC >= 12000
        this.graphics.text(font, text, x, y, color, shadow);
        //#else
        // @formatter:off
            //#if MC >= 11500
            //$$ MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
                //#if MC >= 11900
                //$$ font.drawInBatch(text, x, y, color, shadow, this.poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                //#elseif MC >= 11700
                //$$ font.drawInBatch(text, x, y, color, shadow, this.poseStack.last().pose(), bufferSource, false, 0, 15728880);
                //#elseif MC >= 11600
                //$$ font.drawInBatch(text.getVisualOrderText(), (float)x, (float)y, color, shadow, this.poseStack.last().pose(), bufferSource, false, 0, 15728880);
                //#else
                //$$ com.mojang.math.Matrix4f identityMatrix = new com.mojang.math.Matrix4f();
                //$$ identityMatrix.setIdentity();
                //$$ font.drawInBatch(text.getString(), (float)x, (float)y, color, shadow, identityMatrix, bufferSource, false, 0, 15728880);
                //#endif
            //$$ bufferSource.endBatch();
            //#else
            //$$ if (shadow) {
            //$$     font.drawShadow(text.getString(), (float)x, (float)y, color);
            //$$ } else {
            //$$     font.draw(text.getString(), (float)x, (float)y, color);
            //$$ }
            //#endif
        // @formatter:on
        //#endif
    }

    public void text(Font font, Component text, int x, int y, int color) {
        text(font, text, x, y, color, false);
    }

    public void textWithWordWrap(Font font, Component text, int x, int y, int width, int color) {
        //#if MC >= 12000
        this.graphics.textWithWordWrap(font, text, x, y, width, color);
        //#elseif MC >= 11900
        //$$ font.drawWordWrap(this.poseStack, text, x, y, width, color | 0xFF000000);
        //#elseif MC >= 11600
        //$$ font.drawWordWrap(text, x, y, width, color | 0xFF000000);
        //#else
        //$$ font.drawWordWrap(text.getString(), x, y, width, color | 0xFF000000);
        //#endif
    }

    public void enableScissor(int minX, int minY, int maxX, int maxY) {
        //#if MC >= 12000
        this.graphics.enableScissor(minX, minY, maxX, maxY);
        //#else
        // @formatter:off
            //#if MC >= 11500
            //$$ int scale = (int) Minecraft.getInstance().getWindow().getGuiScale();
            //$$ int windowHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            //#else
            //$$ int scale = (int) Minecraft.getInstance().window.getGuiScale();
            //$$ int windowHeight = Minecraft.getInstance().window.getGuiScaledHeight();
            //#endif

            //#if MC >= 11600
            //$$ GlStateManager._enableScissorTest();
            //$$ GlStateManager._scissorBox(minX * scale, (windowHeight - maxY) * scale, (maxX - minX) * scale, (maxY - minY) * scale);
            //#else
            //$$ org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
            //$$ org.lwjgl.opengl.GL11.glScissor(minX * scale, (windowHeight - maxY) * scale, (maxX - minX) * scale, (maxY - minY) * scale);
            //#endif
        // @formatter:on
        //#endif
    }

    public void disableScissor() {
        //#if MC >= 12000
        this.graphics.disableScissor();
        //#else
        // @formatter:off
        //#if MC >= 11600
        //$$ GlStateManager._disableScissorTest();
        //#else
        //$$ org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
        //#endif
        // @formatter:on
        //#endif
    }

    //#if MC >= 12106
    public void blit(Object pipeline, Identifier texture, int x, int y, float u, float v, int width, int height,
                     int textureWidth, int textureHeight, int color) {
        this.graphics.blit((RenderPipeline) pipeline, texture, x, y, u, v, width, height, textureWidth, textureHeight,
            color);
    }
    //#elseif MC >= 12102
    //$$ public void blit(Function<net.minecraft.resources.ResourceLocation, RenderType> renderType, net.minecraft.resources.ResourceLocation texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, int color) {
    //$$     this.graphics.blit(renderType, texture, x, y, u, v, width, height, textureWidth, textureHeight, color);
    //$$ }
    //#elseif MC >= 12000
    //$$ public void blit(net.minecraft.resources.ResourceLocation texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, float r, float g, float b, float alpha) {
    //$$     RenderSystem.setShaderColor(r, g, b, alpha);
    //$$     this.graphics.blit(texture, x, y, u, v, width, height, textureWidth, textureHeight);
    //$$     RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    //$$ }
    //#else
    //$$ public void blit(net.minecraft.resources.ResourceLocation texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, float r, float g, float b, float alpha) {
    // @formatter:off
        //#if MC >= 11700
        //$$ RenderSystem.setShaderTexture(0, texture);
        //$$ RenderSystem.setShaderColor(r, g, b, alpha);
        //#else
        //$$ Minecraft.getInstance().getTextureManager().bind(texture);
        //$$ org.lwjgl.opengl.GL11.glColor4f(r, g, b, alpha);
        //#endif

        //#if MC >= 11600
        //$$ GuiComponent.blit(this.poseStack, x, y, 0, u, v, width, height, textureWidth, textureHeight);
        //#else
        //$$ GuiComponent.blit(x, y, 0, u, v, width, height, textureWidth, textureHeight);
        //#endif

        //#if MC >= 11700
        //$$ RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        //#else
        //$$ org.lwjgl.opengl.GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        //#endif
    // @formatter:on
    //$$ }
    //#endif
}
