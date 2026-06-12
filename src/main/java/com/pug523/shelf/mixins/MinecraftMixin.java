package com.pug523.shelf.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.pug523.shelf.gui.renderer.SdfRenderQueue;

import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    //#if MC >= 260000
    private static final String targetMethod = "renderFrame";
    //#elseif MC >= 12104
    //$$ private static final String targetMethod = "runTick";
    //#endif

    //#if MC >= 12104
    @Inject(method = targetMethod, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;blitToScreen()V", shift = At.Shift.AFTER))
    private void onAfterBlitToScreen(boolean advanceGameTime, CallbackInfo ci) {
        SdfRenderQueue.flushAll();
    }
    //#endif
}
