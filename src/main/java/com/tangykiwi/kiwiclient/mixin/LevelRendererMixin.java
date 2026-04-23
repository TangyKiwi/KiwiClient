package com.tangykiwi.kiwiclient.mixin;

import java.util.Iterator;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.objectweb.asm.Opcodes;
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
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.LevelRenderEvent;
import com.tangykiwi.kiwiclient.module.render.ESP;
import com.tangykiwi.kiwiclient.module.render.NoRender;
import com.tangykiwi.kiwiclient.util.EntityUtils;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

// change to LevelRenderer
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @WrapWithCondition(method = "extractLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WeatherEffectRenderer;extractRenderState(Lnet/minecraft/world/level/Level;IFLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/state/level/WeatherRenderState;)V"))
    private boolean extractLevel$noWeather(WeatherEffectRenderer instance, Level level, int ticks, float partialTicks, Vec3 cameraPos, WeatherRenderState renderState) {
        NoRender noRender = (NoRender) KiwiClient.moduleManager.getModule(NoRender.class);
        if (noRender.isEnabled()) {
            boolean noWeather = noRender.getSetting("Weather").asToggle().getValue();
            if (noWeather) {
                renderState.intensity = 0;
                return false;
            }
        }
        return true;
    }

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void onRenderLevelHead(GraphicsResourceAllocator resourceAllocator, DeltaTracker deltaTracker, boolean renderOutline, CameraRenderState cameraState, Matrix4fc modelViewMatrix, GpuBufferSlice terrainFog, Vector4f fogColor, boolean shouldRenderSky, ChunkSectionsToRender chunkSectionsToRender, CallbackInfo ci) {
        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(modelViewMatrix);
        LevelRenderEvent event = new LevelRenderEvent(poseStack, deltaTracker.getGameTimeDeltaPartialTick(false));
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

//     @Inject(method = "submitEntities", at = @At("TAIL"), cancellable = true)
//     private void pushEntityRenders(PoseStack matrices, LevelRenderState renderStates, SubmitNodeCollector queue, CallbackInfo ci) {
//         Vec3 vec3d = renderStates.cameraRenderState.pos;
//         double d = vec3d.x();
//         double e = vec3d.y();
//         double f = vec3d.z();

//         EntityRenderState entityRenderState;
//         for(Iterator var11 = renderStates.entityRenderStates.iterator(); var11.hasNext(); this.entityRenderManager.extractRenderState(entityRenderState, renderStates.cameraRenderState, entityRenderState.x - d, entityRenderState.y - e, entityRenderState.z - f, matrices, queue)) {
//             entityRenderState = (EntityRenderState)var11.next();
//             ESP esp = (ESP) KiwiClient.moduleManager.getModule(ESP.class);
//             if (esp.isEnabled() && esp.getSetting("Mode").asMode().getValue() == 0) {
//                 if (entityRenderState.entityType == EntityType.PLAYER) {
//                     entityRenderState.outlineColor = (255 << 24) | (255 << 16) | (255 << 8) | 255;
//                 } else if (EntityUtils.isMob(entityRenderState.entityType)) {
//                     entityRenderState.outlineColor = (255 << 24) | (255 << 16) | (0 << 8) | 0;
//                 } else if (EntityUtils.isAnimal(entityRenderState.entityType)) {
//                     entityRenderState.outlineColor = (255 << 24) | (77 << 16) | (255 << 8) | 77;
//                 } else {
//                     entityRenderState.outlineColor = (255 << 24) | (128 << 16) | (128 << 8) | 128;
//                 }
//             }   
//         }
//    }
}