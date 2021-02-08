package com.tangykiwi.kiwiclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.RenderWorldEvent;
import com.tangykiwi.kiwiclient.modules.player.Freecam;
import com.tangykiwi.kiwiclient.util.CameraEntity;
import com.tangykiwi.kiwiclient.util.CustomMatrix;
import com.tangykiwi.kiwiclient.util.CustomRenderer;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
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

    @Shadow @Final private Camera camera;

    @Nullable
    private Entity cameraEntityOriginal;

    private boolean a = false;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRenderHead(float tickDelta, long startTime, boolean tick, CallbackInfo info) {
        a = false;
    }

    @Inject(method = "renderWorld", at = @At("HEAD"))
    private void onRenderWorldHead(float tickDelta, long limitTime, MatrixStack matrixStack, CallbackInfo info) {
        CustomMatrix.begin(matrixStack);
        CustomMatrix.push();
        RenderSystem.pushMatrix();

        a = true;
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = { "ldc=hand" }))
    private void onRenderWorld(float tickDelta, long limitTime, MatrixStack matrixStack, CallbackInfo info) {
        if (!Utils.canUpdate()) return;

        RenderWorldEvent event = new RenderWorldEvent(tickDelta, camera.getPos().x, camera.getPos().y, camera.getPos().z);

        CustomRenderer.begin(event);
        KiwiClient.eventBus.post(event);
        CustomRenderer.end();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clear(IZ)V", ordinal = 0))
    private void onRenderBeforeGuiRender(float tickDelta, long startTime, boolean tick, CallbackInfo info) {
        if (a) {
            CustomMatrix.pop();
            RenderSystem.popMatrix();
        }
    }

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
