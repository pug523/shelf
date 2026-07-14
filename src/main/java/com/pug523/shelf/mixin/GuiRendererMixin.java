package com.pug523.shelf.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12106
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderPass;
import com.pug523.shelf.gui.renderer.shader.UniformRegistry;
import com.pug523.shelf.gui.renderer.state.SdfRenderState;
import com.pug523.shelf.gui.renderer.RenderPipelines;
import com.pug523.shelf.gui.renderer.SdfParamBufferPool;
import java.util.List;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
//$$ import com.pug523.shelf.Shelf;
//#endif

//#if MC >= 12106
@Mixin(GuiRenderer.class)
//#else
//$$ @Mixin(Shelf.class)
//#endif
public class GuiRendererMixin {
    //#if MC >= 12106
    @Shadow
    @Final
    //#if MC >= 260200
    private List<GuiRenderer.Draw> draws;
    //#else
    //$$ private List<GuiRenderer.MeshToDraw> meshesToDraw;
    //#endif

    // @formatter:off
    //#if MC >= 260200
    @Inject(
        method = "addElementToMesh",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
            shift = At.Shift.AFTER
        )
    )
    //#else
    //$$ @Inject(
    //$$     method = "addElementToMesh",
    //$$     at = @At(
    //$$         value = "INVOKE",
    //$$         target = "Lnet/minecraft/client/gui/render/GuiRenderer;recordMesh(Lcom/mojang/blaze3d/vertex/BufferBuilder;Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/gui/render/TextureSetup;Lnet/minecraft/client/gui/navigation/ScreenRectangle;)V",
    //$$         shift = At.Shift.AFTER
    //$$     )
    //$$ )
    //#endif
    //#if MC >= 12109
    private void registerUniformApplier(GuiElementRenderState elementState, CallbackInfo ci) {
    //#else
    //$$ private void registerUniformApplier(GuiElementRenderState elementState, int i, CallbackInfo ci) {
    //#endif
    // @formatter:on
        if (elementState.pipeline() == RenderPipelines.SDF_PIPELINE) {
            //#if MC >= 260200
            UniformRegistry.put(this.draws.size() - 1, ((SdfRenderState) elementState));
            //#else
            //$$ UniformRegistry.put(this.meshesToDraw.size(), ((SdfRenderState) elementState));
            //#endif
        }
    }

    @ModifyExpressionValue(
        method = "addElementToMesh",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/render/TextureSetup;equals(Ljava/lang/Object;)Z"
        )
    )
    private boolean forceSplitForSdf(boolean original, @Local(argsOnly = true) GuiElementRenderState elementState) {
        if (elementState.pipeline() == RenderPipelines.SDF_PIPELINE) {
            return false;
        }
        return original;
    }

    //#if MC >= 260000
    @Inject(
        method = "executeDrawRange",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;get(I)Ljava/lang/Object;",
            shift = At.Shift.AFTER
        )
    )
    //#else
    //$$ @Inject(
    //$$     method = "executeDrawRange",
    //$$     at = @At(
    //$$         value = "INVOKE",
    //$$         target = "executeDraw(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V"
    //$$     )
    //$$ )
    //#endif
    private void applyUniforms(CallbackInfo ci, @Local(name = "renderPass") RenderPass renderPass,
                               // @formatter:off
                               //#if MC >= 260000
                               @Local(name = "i") int i) {
                               //#else
                               //$$ @Local(name = "k") int i) {
                               //#endif
                               // @formatter:on
        UniformRegistry.applyAndRemove(i, renderPass);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void tryShrinkSdfParams(CallbackInfo ci) {
        SdfParamBufferPool.tryShrink();
        UniformRegistry.clear();
    }
    //#endif
}
