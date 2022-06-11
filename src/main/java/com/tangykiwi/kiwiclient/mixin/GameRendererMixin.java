package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.mixininterface.IVec3d;
import com.tangykiwi.kiwiclient.modules.render.Freecam;
import com.tangykiwi.kiwiclient.modules.render.Zoom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GameRenderer.class, priority = 1001)
public abstract class GameRendererMixin
{
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow public abstract void updateTargetedEntity(float tickDelta);

    private boolean freecamSet = false;

    @Inject(at = @At(value = "RETURN", ordinal = 1),
            method = {"getFov(Lnet/minecraft/client/render/Camera;FZ)D"},
            cancellable = true)
    private void onGetFov(Camera camera, float tickDelta, boolean changingFov,
                          CallbackInfoReturnable<Double> cir)
    {
        cir.setReturnValue(
                ((Zoom) KiwiClient.moduleManager.getModule(Zoom.class)).changeFovBasedOnZoom(cir.getReturnValueD()));
    }

    @Inject(
            method = {"updateTargetedEntity"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void updateTargetedEntity(float tickDelta, CallbackInfo info) {
        Freecam freecam = (Freecam) KiwiClient.moduleManager.getModule(Freecam.class);
        if (!this.freecamSet && freecam.isEnabled() && this.client.getCameraEntity() != null) {
            info.cancel();
            Entity camera = this.client.getCameraEntity();
            double x = camera.getX();
            double y = camera.getY();
            double z = camera.getZ();
            double prevX = camera.prevX;
            double prevY = camera.prevY;
            double prevZ = camera.prevZ;
            float yaw = camera.getYaw();
            float pitch = camera.getPitch();
            float prevYaw = camera.prevYaw;
            float prevPitch = camera.prevPitch;
            ((IVec3d)camera.getPos()).set(freecam.pos.x, freecam.pos.y - (double)camera.getEyeHeight(camera.getPose()), freecam.pos.z);
            camera.prevX = freecam.prevPos.x;
            camera.prevY = freecam.prevPos.y - (double)camera.getEyeHeight(camera.getPose());
            camera.prevZ = freecam.prevPos.z;
            camera.setYaw(freecam.yaw);
            camera.setPitch(freecam.pitch);
            camera.prevYaw = freecam.prevYaw;
            camera.prevPitch = freecam.prevPitch;
            this.freecamSet = true;
            this.updateTargetedEntity(tickDelta);
            this.freecamSet = false;
            ((IVec3d)camera.getPos()).set(x, y, z);
            camera.prevX = prevX;
            camera.prevY = prevY;
            camera.prevZ = prevZ;
            camera.setYaw(yaw);
            camera.setPitch(pitch);
            camera.prevYaw = prevYaw;
            camera.prevPitch = prevPitch;
        }
    }
}
