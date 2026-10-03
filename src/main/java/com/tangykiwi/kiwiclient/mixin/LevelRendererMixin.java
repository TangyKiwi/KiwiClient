package com.tangykiwi.kiwiclient.mixin;

import org.joml.Vector4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.LevelRenderEvent;
import com.tangykiwi.kiwiclient.module.render.NoRender;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.state.level.CameraRenderState;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow @Final
    private EntityRenderDispatcher entityRenderDispatcher;

    @Final
    @Shadow
    private SubmitNodeStorage submitNodeStorage;

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderLevelHead(GraphicsResourceAllocator resourceAllocator, boolean renderOutline, CameraRenderState cameraState, GpuBufferSlice terrainFog, Vector4f fogColor, boolean shouldRenderSky, boolean consistentDepthRequired, CallbackInfo ci) {
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(RenderSystem.getModelViewStack());
        LevelRenderEvent event = new LevelRenderEvent(poseStack, KiwiClient.mc.getDeltaTracker().getGameTimeDeltaPartialTick(false), submitNodeStorage);
        KiwiClient.eventBus.post(event);
    }

    @ModifyExpressionValue(method = "addSkyPass", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/state/level/CameraEntityRenderState;doesMobEffectBlockSky:Z", opcode = Opcodes.GETFIELD))
    private boolean modifyMobEffectBlocksSky(boolean original) {
        NoRender noRender = (NoRender) KiwiClient.moduleManager.getModule(NoRender.class);
        if (noRender.isEnabled() && (noRender.getSetting("Blindness").asToggle().getValue() || noRender.getSetting("Darkness").asToggle().getValue())) {
            return false;
        }
        return original;
    }
}