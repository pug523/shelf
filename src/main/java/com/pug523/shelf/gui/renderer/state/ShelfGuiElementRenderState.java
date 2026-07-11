package com.pug523.shelf.gui.renderer.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

//#if MC >= 12106
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.gui.render.TextureSetup;
//#endif

public interface ShelfGuiElementRenderState
    //#if MC >= 12106
    extends GuiElementRenderState
    //#endif
{
    //#if MC >= 12106
    @Override
    //#endif
    //#if MC >= 12106 && MC <= 12108
    //$$ void buildVertices(@NonNull VertexConsumer vertices, float depth);
    //#else
    void buildVertices(@NonNull VertexConsumer vertices);
    //#endif

    //#if MC >= 12106
    @Override
    //#endif
    @NonNull RenderPipeline pipeline();

    //#if MC >= 12106
    @Override
    default @NonNull TextureSetup textureSetup() {
        return TextureSetup.noTexture();
    }
    //#endif

    //#if MC >= 12106
    @Override
    //#endif
    @Nullable ScreenRectangle scissorArea();

    //#if MC >= 12106
    @Override
    //#endif
    @Nullable ScreenRectangle bounds();
}
