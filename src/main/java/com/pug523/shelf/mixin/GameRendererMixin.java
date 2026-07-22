package com.pug523.shelf.mixin;

import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 11800
import com.pug523.shelf.Shelf;
//#else
//$$ import com.pug523.shelf.ui.render.shader.ShaderManager;
//$$ import net.minecraft.client.renderer.GameRenderer;
//$$ import net.minecraft.server.packs.resources.ResourceManager;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

/// In 1.17.1 or below, there is neither automated shader loading (1.21.2+)
/// nor CoreShaderRegistrationCallback (Fabric-API),
/// so we have to load ui shaders manually with this mixin.

//#if MC >= 11800
@Mixin(Shelf.class)
//#else
//$$ @Mixin(GameRenderer.class)
//#endif
public class GameRendererMixin {
    //#if MC <= 11701
    //$$ @Inject(
    //$$     method = "reloadShaders(Lnet/minecraft/server/packs/resources/ResourceManager;)V",
    //$$     at = @At("TAIL")
    //$$ )
    //$$ private void loadShelfUiShaders(ResourceManager resourceManager, CallbackInfo Ci) {
    //$$     ShaderManager.loadSdfShader(resourceManager);
    //$$ }
    //$$ @Inject(
    //$$     method = "shutdownShaders()V",
    //$$     at = @At("TAIL")
    //$$ )
    //$$ private void shutdownShelfUiShaders(CallbackInfo Ci) {
    //$$     ShaderManager.closeSdfShader();
    //$$ }
    //#endif
}
