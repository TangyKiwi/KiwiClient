package com.tangykiwi.kiwiclient.mixin;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.WorldRenderEvent;
import com.tangykiwi.kiwiclient.module.render.NoRender;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WeatherRendering;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @WrapWithCondition(method = "method_62216", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WeatherRendering;renderPrecipitation(Lnet/minecraft/world/World;Lnet/minecraft/client/render/VertexConsumerProvider;IFLnet/minecraft/util/math/Vec3d;)V"))
    private boolean shouldRenderPrecipitation(WeatherRendering instance, World world, VertexConsumerProvider vertexConsumers, int ticks, float tickProgress, Vec3d pos) {
        NoRender noRender = (NoRender) KiwiClient.moduleManager.getModule(NoRender.class);
        if (noRender.isEnabled()) {
            return !noRender.getSetting("Weather").asToggle().getValue();
        }
        return true;
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void onWorldRenderHead(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fog, Vector4f fogColor, boolean shouldRenderSky, CallbackInfo ci) {
    
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void onWorldRenderTail(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fog, Vector4f fogColor, boolean shouldRenderSky, CallbackInfo ci) {
        WorldRenderEvent.Post event = new WorldRenderEvent.Post(positionMatrix, projectionMatrix);
        KiwiClient.eventBus.post(event);
    }

    @Inject(method = "hasBlindnessOrDarkness(Lnet/minecraft/client/render/Camera;)Z", at = @At("HEAD"), cancellable = true)
	private void hasBlindnessOrDarkness(Camera camera, CallbackInfoReturnable<Boolean> info) {
		if (KiwiClient.moduleManager.getModule(NoRender.class).getSetting("Blindness").asToggle().getValue() || KiwiClient.moduleManager.getModule(NoRender.class).getSetting("Darkness").asToggle().getValue()) info.setReturnValue(null);
	}
}