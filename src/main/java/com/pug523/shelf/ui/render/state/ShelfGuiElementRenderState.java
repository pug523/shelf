package com.pug523.shelf.ui.render.state;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

//#if MC >= 12106
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.gui.render.TextureSetup;
//#endif

//#if MC >= 12104
import com.mojang.blaze3d.pipeline.RenderPipeline;
//#endif

//#if MC >= 11900
import net.minecraft.client.gui.navigation.ScreenRectangle;
//#endif

public interface ShelfGuiElementRenderState
    //#if MC >= 12106
    extends GuiElementRenderState
    //#endif
{
    //#if MC >= 12106 && MC <= 12108
    //$$ @Override
    //$$ default void buildVertices(@NonNull VertexConsumer vertices, float depth) {
    //$$     buildVertices(vertices);
    //$$ }
    //#endif

    //#if MC >= 12109
    @Override
    //#endif
    void buildVertices(@NonNull VertexConsumer vertices);

    //#if MC >= 12104
    //#if MC >= 12106
    @Override
    //#endif
    @NonNull RenderPipeline pipeline();
    //#endif

    //#if MC >= 12106
    @Override
    default @NonNull TextureSetup textureSetup() {
        return TextureSetup.noTexture();
    }
    //#endif

    //#if MC >= 11900
    //#if MC >= 12106
    @Override
    //#endif
    @Nullable ScreenRectangle scissorArea();

    //#if MC >= 12106
    @Override
    //#endif
    @Nullable ScreenRectangle bounds();
    //#endif
}
