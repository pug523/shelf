package com.pug523.shelf.mixin;

import org.spongepowered.asm.mixin.Mixin;

// @formatter:off
//#if MC <= 12105
//$$ import net.minecraft.client.renderer.RenderType;
//$$ import com.pug523.shelf.gui.renderer.RenderPipelines;
//$$ import com.pug523.shelf.gui.renderer.shader.UniformRegistry;
//$$ import com.mojang.blaze3d.vertex.MeshData;
//$$ import com.llamalad7.mixinextras.sugar.Local;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Shadow;
    //#if MC >= 12104
    //$$ import com.mojang.blaze3d.pipeline.RenderPipeline;
    //$$ import com.mojang.blaze3d.systems.RenderPass;
    //#endif
//#else
import com.pug523.shelf.Shelf;
//#endif
// @formatter:on

//#if MC <= 12105
//$$ @Mixin(targets = "net.minecraft.client.renderer.RenderType$CompositeRenderType")
//#else
@Mixin(Shelf.class)
//#endif
public class CompositeRenderTypeMixin {
    // @formatter:off
    //#if MC <= 12105

        //#if MC >= 12104
        //$$ @Shadow @Final private RenderPipeline renderPipeline;
        //#endif

    //$$ @Inject(
    //$$         method = "draw(Lnet/mojang/blaze3d/vertex/MeshData;)V",
    //$$         at = @At(
    //$$             value = "INVOKE",
    //$$             target = "Lcom/mojang/blaze3d/systems/RenderPass;drawIndexed(II)V"
    //$$         )
    //$$ )
    //$$ private void onDraw(MeshData meshData, CallbackInfo ci, @Local RenderPass renderPass) {
    //$$     if (this.renderPipeline == RenderPipelines.SDF_PIPELINE) {
    //$$         UniformRegistry.applyIfPresent(renderPass);
    //$$     }
    //$$ }
    //#endif
    // @formatter:on
}
