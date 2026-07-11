package com.pug523.shelf.mixin;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12106
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderPass;
import com.pug523.shelf.gui.renderer.shader.UniformApplier;
import com.pug523.shelf.gui.renderer.shader.UniformRegistry;
import com.pug523.shelf.gui.renderer.state.SdfRenderState;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiRenderer.class)
//#else
//$$ import com.pug523.shelf.Shelf;
//$$ @Mixin(Shelf.class)
//#endif
public class GuiRendererMixin {
    //#if MC >= 12106
    @Shadow
    @Final
    private List<GuiRenderer.Draw> draws;

    //#if MC >= 260200
    @Inject(method = "addElementToMesh", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    //#else
    //$$ @Inject(method = "addElementToMesh", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;recordMesh(Lcom/mojang/blaze3d/vertex/BufferBuilder;Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/gui/render/TextureSetup;Lnet/minecraft/client/gui/navigation/ScreenRectangle;)V"))
    //#endif

    // @formatter:off
    //#if MC >= 12109
    private void registerUniformApplier(GuiElementRenderState elementState, CallbackInfo ci) {
    //#else
    //$$ private void registerUniformApplier(GuiElementRenderState elementState, int i, CallbackInfo ci) {
    //#endif
    // @formatter:on
        if (elementState instanceof SdfRenderState) {
            UniformRegistry.put(this.draws.size(), ((SdfRenderState) elementState));
        }
    }

    @Inject(method = "executeDrawRange", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", shift = At.Shift.AFTER))
    private void applyUniforms(CallbackInfo ci, @Local(name = "renderPass") RenderPass renderPass,
                               @Local(name = "i") int i) {
        UniformApplier e = UniformRegistry.get(i);
        if (e != null) {
            e.applyUniforms(renderPass);
            UniformRegistry.remove(i);
        }
    }

    // @Inject(method = "executeDrawRange", at = @At("TAIL"))
    // private void clearUniformRegistry(CallbackInfo ci) {
    //     UniformRegistry.clear();
    // }

    //#endif
}
