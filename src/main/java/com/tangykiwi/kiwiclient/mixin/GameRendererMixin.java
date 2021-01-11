package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.player.Freecam;
import com.tangykiwi.kiwiclient.util.CameraEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GameRenderer.class, priority = 1001)
public abstract class GameRendererMixin
{
    @Shadow
    @Final
    private MinecraftClient client;

    @Nullable
    private Entity cameraEntityOriginal;

    @Inject(method = "renderWorld", at = @At(
            value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/render/GameRenderer;updateTargetedEntity(F)V"))
    private void overrideRenderViewEntityPre(CallbackInfo ci)
    {
        if (KiwiClient.moduleManager.getModule(Freecam.class).isEnabled())
        {
            Entity camera = CameraEntity.getCamera();

            if (camera != null)
            {
                this.cameraEntityOriginal = this.client.getCameraEntity();
                this.client.setCameraEntity(camera);
            }
        }
    }

    @Inject(method = "renderWorld", at = @At("RETURN"))
    private void overrideRenderViewEntityPost(CallbackInfo ci)
    {
        if (KiwiClient.moduleManager.getModule(Freecam.class).isEnabled() && this.cameraEntityOriginal != null)
        {
            this.client.setCameraEntity(this.cameraEntityOriginal);
            this.cameraEntityOriginal = null;
        }
    }
}
