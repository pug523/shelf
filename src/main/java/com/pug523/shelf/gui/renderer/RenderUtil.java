package com.pug523.shelf.gui.renderer;

import com.pug523.shelf.compat.GuiCompat;
import com.pug523.shelf.gui.renderer.state.SdfRenderState;
import com.pug523.shelf.gui.renderer.state.ShelfGuiElementRenderState;
import com.pug523.shelf.gui.renderer.state.VanillaRectangleRenderState;
import com.pug523.shelf.Constants;

//#if MC <= 12105
//$$ import com.pug523.shelf.gui.renderer.shader.SdfRenderTypeCache;
//$$ import net.minecraft.client.renderer.RenderType;
//#endif

//#if MC <= 11904
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//$$ import com.mojang.blaze3d.vertex.BufferBuilder;
//$$ import com.mojang.blaze3d.vertex.BufferUploader;
//$$ import com.mojang.blaze3d.vertex.Tesselator;
//$$ import net.minecraft.client.renderer.GameRenderer;
//#endif

public class RenderUtil {
    private RenderUtil() {
    }

    private static final int[] RAINBOW_COLORS = new int[]{0xFFFF0000, 0xFFFFFF00, 0xFF00FF00, 0xFF00FFFF, 0xFF0000FF, 0xFFFF00FF};

    private static ShelfGuiElementRenderState createRectState(GuiCompat gui, float x0, float y0, float x1, float y1, float radius, int x0y0, int x0y1, int x1y0, int x1y1) {
        if (Constants.SDF_SHADER_AVAILABLE) {
            SdfRenderState state = new SdfRenderState(gui, x0, y0, x1, y1, x1 - x0, y1 - y0, radius, x0y0, x0y1, x1y0, x1y1);
            //#if MC >= 11900
            state.setRectangles(gui, gui.peekScissorStack());
            //#endif
            return state;
        } else {
            VanillaRectangleRenderState state = new VanillaRectangleRenderState(gui, (int)x0, (int)x1, (int)y0, (int)y1, x0y0, x0y1, x1y0, x1y1);
            //#if MC >= 11900
            state.setRectangles(gui, gui.peekScissorStack());
            //#endif
            return state;
        }
    }

    /**
     * Renders a solid color circle.
     *
     * @param gui     The GUI compatibility helper context
     * @param centerX The X coordinate of the circle's center
     * @param centerY The Y coordinate of the circle's center
     * @param radius  The radius of the circle
     * @param color   The packed ARGB color
     */
    public static void renderCircle(GuiCompat gui, float centerX, float centerY, float radius, int color) {
        float diameter = radius * 2.0f;
        float x = centerX - radius;
        float y = centerY - radius;
        addRenderState(gui, createRectState(gui, x, y, x + diameter, y + diameter, radius, color, color, color, color));
    }

    /**
     * Renders a solid color capsule (a rectangle with fully rounded ends based on height).
     *
     * @param gui    The GUI compatibility helper context
     * @param x      The starting X coordinate
     * @param y      The starting Y coordinate
     * @param width  The width of the capsule
     * @param height The height of the capsule (also dictates corner radius)
     * @param color  The packed ARGB color
     */
    public static void renderCapsule(GuiCompat gui, float x, float y, float width, float height, int color) {
        float radius = height / 2.0f;
        renderRoundedRect(gui, x, y, width, height, radius, color);
    }

    /**
     * Renders a capsule featuring a horizontal color gradient.
     *
     * @param gui        The GUI compatibility helper context
     * @param x          The starting X coordinate
     * @param y          The starting Y coordinate
     * @param width      The width of the capsule
     * @param height     The height of the capsule
     * @param startColor The starting packed ARGB color (left side)
     * @param endColor   The ending packed ARGB color (right side)
     */
    public static void renderCapsuleHorizontal(GuiCompat gui, float x, float y, float width, float height, int startColor, int endColor) {
        float radius = height / 2.0f;
        renderRoundedRectHorizontal(gui, x, y, width, height, radius, startColor, endColor);
    }

    /**
     * Renders a capsule featuring a vertical color gradient.
     *
     * @param gui        The GUI compatibility helper context
     * @param x          The starting X coordinate
     * @param y          The starting Y coordinate
     * @param width      The width of the capsule
     * @param height     The height of the capsule
     * @param startColor The starting packed ARGB color (top side)
     * @param endColor   The ending packed ARGB color (bottom side)
     */
    public static void renderCapsuleVertical(GuiCompat gui, float x, float y, float width, float height, int startColor, int endColor) {
        float radius = height / 2.0f;
        renderRoundedRectVertical(gui, x, y, width, height, radius, startColor, endColor);
    }

    /**
     * Renders a partial slice/segment of a capsule using localized canvas bounding vectors.
     * Falls back to a standard flat rectangle render state if SDF shaders are unavailable.
     *
     * @param gui          The GUI compatibility helper context
     * @param sliceX0      The local bounds starting X coordinate
     * @param sliceY0      The local bounds starting Y coordinate
     * @param sliceX1      The local bounds ending X coordinate
     * @param sliceY1      The local bounds ending Y coordinate
     * @param globalX      The true global base X coordinate of the entire component canvas
     * @param globalY      The true global base Y coordinate of the entire component canvas
     * @param globalWidth  The absolute total width of the overall parent capsule structure
     * @param globalHeight The absolute total height of the overall parent capsule structure
     * @param color        The packed ARGB color
     */
    public static void renderCapsulePartFlat(GuiCompat gui, float sliceX0, float sliceY0, float sliceX1, float sliceY1,
                                             float globalX, float globalY, float globalWidth, float globalHeight, int color) {
        float radius = globalHeight / 2.0f;

        if (!Constants.SDF_SHADER_AVAILABLE) {
            addRenderState(gui, createRectState(gui, sliceX0, sliceY0, sliceX1, sliceY1, 0, color, color, color, color));
            return;
        }

        float u0 = (sliceX0 - globalX) / globalWidth;
        float u1 = (sliceX1 - globalX) / globalWidth;
        float v0 = (sliceY0 - globalY) / globalHeight;
        float v1 = (sliceY1 - globalY) / globalHeight;

        SdfRenderState renderState = new SdfRenderState(
            gui, sliceX0, sliceY0, sliceX1, sliceY1, globalWidth, globalHeight, radius,
            u0, v0, u1, v1, color, color, color, color
        );
        //#if MC >= 11900
        renderState.setRectangles(gui, gui.peekScissorStack());
        //#endif
        renderGuiElementDispatcher(gui, renderState);
    }

    /**
     * Renders a standard sharp-cornered solid rectangle.
     *
     * @param gui    The GUI compatibility helper context
     * @param x      The starting X coordinate
     * @param y      The starting Y coordinate
     * @param width  The width of the rectangle
     * @param height The height of the rectangle
     * @param color  The packed ARGB color
     */
    public static void renderRect(GuiCompat gui, float x, float y, float width, float height, int color) {
        renderRoundedRect(gui, x, y, width, height, 0, color);
    }

    /**
     * Renders a solid color rectangle with custom rounded corners.
     *
     * @param gui    The GUI compatibility helper context
     * @param x      The starting X coordinate
     * @param y      The starting Y coordinate
     * @param width  The width of the rectangle
     * @param height The height of the rectangle
     * @param radius The radius used to round the corners
     * @param color  The packed ARGB color
     */
    public static void renderRoundedRect(GuiCompat gui, float x, float y, float width, float height, float radius, int color) {
        addRenderState(gui, createRectState(gui, x, y, x + width, y + height, radius, color, color, color, color));
    }

    /**
     * Renders a sharp-cornered rectangle featuring a horizontal color gradient.
     *
     * @param gui        The GUI compatibility helper context
     * @param x          The starting X coordinate
     * @param y          The starting Y coordinate
     * @param width      The width of the rectangle
     * @param height     The height of the rectangle
     * @param startColor The starting packed ARGB color (left side)
     * @param endColor   The ending packed ARGB color (right side)
     */
    public static void renderRectHorizontal(GuiCompat gui, float x, float y, float width, float height, int startColor, int endColor) {
        renderRoundedRectHorizontal(gui, x, y, width, height, 0, startColor, endColor);
    }

    /**
     * Renders a rounded-corner rectangle featuring a horizontal color gradient.
     *
     * @param gui        The GUI compatibility helper context
     * @param x          The starting X coordinate
     * @param y          The starting Y coordinate
     * @param width      The width of the rectangle
     * @param height     The height of the rectangle
     * @param radius     The radius used to round the corners
     * @param startColor The starting packed ARGB color (left side)
     * @param endColor   The ending packed ARGB color (right side)
     */
    public static void renderRoundedRectHorizontal(GuiCompat gui, float x, float y, float width, float height, float radius, int startColor, int endColor) {
        addRenderState(gui, createRectState(gui, x, y, x + width, y + height, radius, startColor, startColor, endColor, endColor));
    }

    /**
     * Renders a sharp-cornered rectangle featuring a vertical color gradient.
     *
     * @param gui        The GUI compatibility helper context
     * @param x          The starting X coordinate
     * @param y          The starting Y coordinate
     * @param width      The width of the rectangle
     * @param height     The height of the rectangle
     * @param startColor The starting packed ARGB color (top side)
     * @param endColor   The ending packed ARGB color (bottom side)
     */
    public static void renderRectVertical(GuiCompat gui, float x, float y, float width, float height, int startColor, int endColor) {
        renderRoundedRectVertical(gui, x, y, width, height, 0, startColor, endColor);
    }

    /**
     * Renders a rounded-corner rectangle featuring a vertical color gradient.
     *
     * @param gui        The GUI compatibility helper context
     * @param x          The starting X coordinate
     * @param y          The starting Y coordinate
     * @param width      The width of the rectangle
     * @param height     The height of the rectangle
     * @param radius     The radius used to round the corners
     * @param startColor The starting packed ARGB color (top side)
     * @param endColor   The ending packed ARGB color (bottom side)
     */
    public static void renderRoundedRectVertical(GuiCompat gui, float x, float y, float width, float height, float radius, int startColor, int endColor) {
        addRenderState(gui, createRectState(gui, x, y, x + width, y + height, radius, startColor, endColor, startColor, endColor));
    }

    /**
     * Draws an un-filled hollow rectangle boundary outline using four individual line strips.
     *
     * @param gui             The GUI compatibility helper context
     * @param x               The starting X coordinate
     * @param y               The starting Y coordinate
     * @param width           The total outer width of the outline bounding area
     * @param height          The total outer height of the outline bounding area
     * @param borderThickness The stroke width thickness inward from the outer boundaries
     * @param color           The packed ARGB color
     */
    public static void renderOutline(GuiCompat gui, int x, int y, int width, int height, int borderThickness, int color) {
        gui.fill(x, y, x + borderThickness, y + height, color);
        gui.fill(x + width - borderThickness, y, x + width, y + height, color);
        gui.fill(x + borderThickness, y, x + width - borderThickness, y + borderThickness, color);
        gui.fill(x + borderThickness, y + height - borderThickness, x + width - borderThickness, y + height, color);
    }

    /**
     * Fills an inner area of a bounded rectangle, contracting borders based on thickness.
     *
     * @param gui             The GUI compatibility helper context
     * @param x               The starting X coordinate
     * @param y               The starting Y coordinate
     * @param width           The outer layout canvas width
     * @param height          The outer layout canvas height
     * @param borderThickness The layout indentation width applied onto all 4 inside edges
     * @param color           The packed ARGB color
     */
    public static void renderInner(GuiCompat gui, int x, int y, int width, int height, int borderThickness, int color) {
        gui.fill(x + borderThickness, y + borderThickness, x + width - borderThickness, y + height - borderThickness, color);
    }

    /**
     * Renders a simple pixelated flat arrow point down (5x3 block bounding shape).
     *
     * @param gui    The GUI compatibility helper context
     * @param startX The anchor left X coordinate
     * @param startY The anchor upper Y coordinate
     * @param color  The packed ARGB color
     */
    public static void renderDownwardArrow(GuiCompat gui, int startX, int startY, int color) {
        gui.fill(startX, startY, startX + 5, startY + 1, color);
        gui.fill(startX + 1, startY + 1, startX + 4, startY + 2, color);
        gui.fill(startX + 2, startY + 2, startX + 3, startY + 3, color);
    }

    /**
     * Renders a simple pixelated flat arrow point right (3x5 block bounding shape).
     *
     * @param gui    The GUI compatibility helper context
     * @param startX The anchor left X coordinate
     * @param startY The anchor upper Y coordinate
     * @param color  The packed ARGB color
     */
    public static void renderRightwardArrow(GuiCompat gui, int startX, int startY, int color) {
        gui.fill(startX, startY, startX + 1, startY + 5, color);
        gui.fill(startX + 1, startY + 1, startX + 2, startY + 4, color);
        gui.fill(startX + 2, startY + 2, startX + 3, startY + 3, color);
    }

    /**
     * Renders a flat sharp-cornered rectangle filled with a multi-step horizontal rainbow gradient loop.
     *
     * @param gui    The GUI compatibility helper context
     * @param x      The starting X coordinate
     * @param y      The starting Y coordinate
     * @param width  The width of the gradient bar
     * @param height The height of the gradient bar
     */
    public static void renderRainbowGradientHorizontal(GuiCompat gui, int x, int y, int width, int height) {
        renderRainbowGradient(gui, x, y, width, height, true, false);
    }

    /**
     * Renders a flat sharp-cornered rectangle filled with a multi-step vertical rainbow gradient loop.
     *
     * @param gui    The GUI compatibility helper context
     * @param x      The starting X coordinate
     * @param y      The starting Y coordinate
     * @param width  The width of the gradient bar
     * @param height The height of the gradient bar
     */
    public static void renderRainbowGradientVertical(GuiCompat gui, int x, int y, int width, int height) {
        renderRainbowGradient(gui, x, y, width, height, false, false);
    }

    /**
     * Renders a rounded-corner capsule shape filled with a multi-step horizontal rainbow gradient loop.
     *
     * @param gui    The GUI compatibility helper context
     * @param x      The starting X coordinate
     * @param y      The starting Y coordinate
     * @param width  The width of the gradient bar
     * @param height The height of the gradient bar
     */
    public static void renderRoundedRainbowGradientHorizontal(GuiCompat gui, int x, int y, int width, int height) {
        renderRainbowGradient(gui, x, y, width, height, true, true);
    }

    /**
     * Renders a rounded-corner capsule shape filled with a multi-step vertical rainbow gradient loop.
     *
     * @param gui    The GUI compatibility helper context
     * @param x      The starting X coordinate
     * @param y      The starting Y coordinate
     * @param width  The width of the gradient bar
     * @param height The height of the gradient bar
     */
    public static void renderRoundedRainbowGradientVertical(GuiCompat gui, int x, int y, int width, int height) {
        renderRainbowGradient(gui, x, y, width, height, false, true);
    }

    private static void renderRainbowGradient(GuiCompat gui, int x, int y, int width, int height, boolean horizontal, boolean round) {
        float step = (float) (horizontal ? width : height) / RAINBOW_COLORS.length;
        float radius = round ? (horizontal ? (float) height / 2.0f : (float) width / 2.0f) : 0.0f;

        for (int i = 0; i < RAINBOW_COLORS.length; i++) {
            int startColor = RAINBOW_COLORS[i];
            int endColor = RAINBOW_COLORS[(i + 1) % RAINBOW_COLORS.length];

            int x0 = horizontal ? x + (int) (i * step) : x;
            int x1 = horizontal ? x + (int) ((i + 1) * step) : x + width;
            int y0 = horizontal ? y : y + (int) (i * step);
            int y1 = horizontal ? y + height : y + (int) ((i + 1) * step);

            int x0y0 = startColor;
            int x0y1 = horizontal ? startColor : endColor;
            int x1y0 = horizontal ? endColor : startColor;
            int x1y1 = endColor;

            if (!Constants.SDF_SHADER_AVAILABLE) {
                addRenderState(gui, createRectState(gui, x0, y0, x1, y1, radius, x0y0, x0y1, x1y0, x1y1));
                continue;
            }

            float u0 = horizontal ? (float) (i) / RAINBOW_COLORS.length : 0.0f;
            float u1 = horizontal ? (float) (i + 1) / RAINBOW_COLORS.length : 1.0f;
            float v0 = horizontal ? 0.0f : (float) (i) / RAINBOW_COLORS.length;
            float v1 = horizontal ? 1.0f : (float) (i + 1) / RAINBOW_COLORS.length;

            SdfRenderState renderState = new SdfRenderState(
                gui, x0, y0, x1, y1, (float) width, (float) height, radius,
                u0, v0, u1, v1, x0y0, x0y1, x1y0, x1y1
            );
            //#if MC >= 11900
            renderState.setRectangles(gui, gui.peekScissorStack());
            //#endif
            renderGuiElementDispatcher(gui, renderState);
        }
    }

    /// Linearly interpolates between two ARGB packed colors.
    ///
    /// @param startColor The starting color (packed 0xAARRGGBB)
    /// @param endColor   The ending color (packed 0xAARRGGBB)
    /// @param pct        The interpolation percentage weight (0.0 to 1.0)
    /// @return The interpolated ARGB color integer
    public static int linearInterpolateColors(int startColor, int endColor, float pct) {
        // Unpack channels for startColor
        int a1 = (startColor >>> 24) & 0xFF;
        int r1 = (startColor >> 16) & 0xFF;
        int g1 = (startColor >> 8) & 0xFF;
        int b1 = startColor & 0xFF;

        int a2 = (endColor >>> 24) & 0xFF;
        int r2 = (endColor >> 16) & 0xFF;
        int g2 = (endColor >> 8) & 0xFF;
        int b2 = endColor & 0xFF;

        int a = (int) (a1 + (a2 - a1) * pct);
        int r = (int) (r1 + (r2 - r1) * pct);
        int g = (int) (g1 + (g2 - g1) * pct);
        int b = (int) (b1 + (b2 - b1) * pct);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static void renderGuiElementDispatcher(GuiCompat gui, ShelfGuiElementRenderState renderState) {
        if (renderState instanceof SdfRenderState sdfState) {
            renderSdfGuiElement(gui, sdfState);
        } else {
            renderVanillaGuiElement(gui, renderState);
        }
    }

    /**
     * Submits an SDF-based element layout down to the target graphic framework layers.
     * Contains preprocessor version forks adapting from modern GUI pipelines down to archaic legacy setup layers.
     *
     * @param gui         The GUI compatibility helper context
     * @param renderState The generated Signed Distance Field render configurations tracking boundary math
     */
    public static void renderSdfGuiElement(GuiCompat gui, SdfRenderState renderState) {
        //#if MC >= 12106
        gui.getGraphics().guiRenderState.addGuiElement(renderState);
        //#elseif MC >= 12000
        //$$ renderGuiElement(gui, renderState, SdfRenderTypeCache.get(RenderTypes.SDF_RENDER_TYPE, renderState));
        //#else
        //$$ RenderSystem.setShader(RenderTypes::compiledSdfShader);
        //$$ renderGuiElement(gui, renderState, SdfRenderTypeCache.get(RenderTypes.SDF_RENDER_TYPE, renderState));
        //#endif
    }

    /**
     * Submits standard geometric flat polygon vertices down to the target rendering engine layer.
     * Contains version preprocessors managing engine graphic pipelines across modern and retro instances.
     *
     * @param gui         The GUI compatibility helper context
     * @param renderState The element render structural state specifying flat vertex/color data arrays
     */
    public static void renderVanillaGuiElement(GuiCompat gui, ShelfGuiElementRenderState renderState) {
        //#if MC >= 12106
        gui.getGraphics().guiRenderState.addGuiElement(renderState);
        //#elseif MC >= 12000
        //$$ renderGuiElement(gui, renderState, RenderType.gui());
        //#else
        //$$ RenderSystem.setShader(GameRenderer::getPositionColorShader);
        //$$ renderGuiElement(gui, renderState, RenderType.lightning());
        //#endif
    }

    private static void addRenderState(GuiCompat gui, ShelfGuiElementRenderState renderState) {
        renderGuiElementDispatcher(gui, renderState);
    }

    // @formatter:off
    //#if MC <= 12105
    //$$ private static void renderGuiElement(GuiCompat gui, ShelfGuiElementRenderState renderState, RenderType renderType) {
    //#if MC >= 12102
    //$$ gui.getGraphics().drawSpecial(bufferSource -> {
    //$$     renderState.buildVertices(bufferSource.getBuffer(renderType));
    //$$ });
    //#elseif MC >= 12000
    //$$ renderState.buildVertices(gui.getGraphics().bufferSource().getBuffer(renderType));
    //#else
    //$$ BufferBuilder builder = Tesselator.getInstance().getBuilder();
    //$$ builder.begin(renderType.mode(), renderType.format());
    //$$ renderState.buildVertices(builder);
    //#if MC >= 11900
    //$$ BufferUploader.drawWithShader(builder.end());
    //#else
    //$$ Tesselator.getInstance().end();
    //#endif
    //#endif
    //$$ }
    //#endif
    // @formatter:on

}
