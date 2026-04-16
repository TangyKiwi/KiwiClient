package com.tangykiwi.kiwiclient.mixin;

import java.util.Iterator;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.WorldRenderEvent;
import com.tangykiwi.kiwiclient.module.render.ESP;
import com.tangykiwi.kiwiclient.module.render.NoRender;
import com.tangykiwi.kiwiclient.util.EntityUtils;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WeatherRendering;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.WeatherRenderState;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

// change to LevelRenderer
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow
    @Final
    private EntityRenderManager entityRenderManager;

    @WrapWithCondition(method = "method_62216", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WeatherRendering;renderPrecipitation(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/client/render/state/WeatherRenderState;)V"))
    private boolean shouldRenderPrecipitation(WeatherRendering instance, VertexConsumerProvider vertexConsumers, Vec3d pos, WeatherRenderState weatherRenderState) {
        NoRender noRender = (NoRender) KiwiClient.moduleManager.getModule(NoRender.class);
        if (noRender.isEnabled()) {
            return !noRender.getSetting("Weather").asToggle().getValue();
        }
        return true;
    }

    // @Inject(method = "render", at = @At("HEAD"))
    // public void onWorldRenderHead(ObjectAllocator allocator, RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fog, Vector4f fogColor, boolean shouldRenderSky, CallbackInfo ci) {
    
    // }

    @Inject(method = "render", at = @At("TAIL"))
    public void onWorldRenderTail(ObjectAllocator allocator,
                                    RenderTickCounter tickCounter,
                                    boolean renderBlockOutline,
                                    Camera camera,
                                    Matrix4f positionMatrix,
                                    Matrix4f matrix4f,
                                    Matrix4f projectionMatrix,
                                    GpuBufferSlice fog,
                                    Vector4f fogColor,
                                    boolean shouldRenderSky,
                                    CallbackInfo ci) {
        WorldRenderEvent.Post event = new WorldRenderEvent.Post(positionMatrix, projectionMatrix);
        KiwiClient.eventBus.post(event);
    }

    @Inject(method = "hasBlindnessOrDarkness(Lnet/minecraft/client/render/Camera;)Z", at = @At("HEAD"), cancellable = true)
	private void hasBlindnessOrDarkness(Camera camera, CallbackInfoReturnable<Boolean> info) {
		if (KiwiClient.moduleManager.getModule(NoRender.class).getSetting("Blindness").asToggle().getValue() || KiwiClient.moduleManager.getModule(NoRender.class).getSetting("Darkness").asToggle().getValue()) info.setReturnValue(null);
	}

    @Inject(method = "pushEntityRenders", at = @At("TAIL"), cancellable = true)
    private void pushEntityRenders(MatrixStack matrices, WorldRenderState renderStates, OrderedRenderCommandQueue queue, CallbackInfo ci) {
        Vec3d vec3d = renderStates.cameraRenderState.pos;
        double d = vec3d.getX();
        double e = vec3d.getY();
        double f = vec3d.getZ();

        EntityRenderState entityRenderState;
        for(Iterator var11 = renderStates.entityRenderStates.iterator(); var11.hasNext(); this.entityRenderManager.render(entityRenderState, renderStates.cameraRenderState, entityRenderState.x - d, entityRenderState.y - e, entityRenderState.z - f, matrices, queue)) {
            entityRenderState = (EntityRenderState)var11.next();
            ESP esp = (ESP) KiwiClient.moduleManager.getModule(ESP.class);
            if (esp.isEnabled() && esp.getSetting("Mode").asMode().getValue() == 0) {
                if (entityRenderState.entityType == EntityType.PLAYER) {
                    entityRenderState.outlineColor = (255 << 24) | (255 << 16) | (255 << 8) | 255;
                } else if (EntityUtils.isMob(entityRenderState.entityType)) {
                    entityRenderState.outlineColor = (255 << 24) | (255 << 16) | (0 << 8) | 0;
                } else if (EntityUtils.isAnimal(entityRenderState.entityType)) {
                    entityRenderState.outlineColor = (255 << 24) | (77 << 16) | (255 << 8) | 77;
                } else {
                    entityRenderState.outlineColor = (255 << 24) | (128 << 16) | (128 << 8) | 128;
                }
            }   
        }
   }
}