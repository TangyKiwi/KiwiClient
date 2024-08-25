package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.EntityRenderEvent;
import com.tangykiwi.kiwiclient.event.WorldRenderEvent;
import com.tangykiwi.kiwiclient.modules.player.AntiBlind;
import com.tangykiwi.kiwiclient.modules.render.ESP;
import com.tangykiwi.kiwiclient.modules.render.NoRender;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    private void onRenderWeather(LightmapTextureManager manager, float f, double d, double e, double g, CallbackInfo info) {
        if (KiwiClient.moduleManager.getModule(NoRender.class).isEnabled() && KiwiClient.moduleManager.getModule(NoRender.class).getSetting(1).asToggle().state) info.cancel();
    }

    @Inject(method = "hasBlindnessOrDarkness(Lnet/minecraft/client/render/Camera;)Z", at = @At("HEAD"), cancellable = true)
    private void hasBlindnessOrDarkness(Camera camera, CallbackInfoReturnable<Boolean> info) {
        if (KiwiClient.moduleManager.getModule(AntiBlind.class).isEnabled()) info.setReturnValue(null);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"))
    private void render_swap(Profiler profiler, String string) {
        if (string.equals("entities")) {
            KiwiClient.eventBus.post(new EntityRenderEvent.PreAll());
        } else if (string.equals("blockentities")) {
            KiwiClient.eventBus.post(new EntityRenderEvent.PostAll());
            //KiwiClient.eventBus.post(new EventBlockEntityRender.PreAll());
        } else if (string.equals("destroyProgress")) {
            //KiwiClient.eventBus.post(new EventBlockEntityRender.PostAll());
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render_head(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        WorldRenderEvent.Pre event = new WorldRenderEvent.Pre(tickCounter.getTickDelta(true));
        KiwiClient.eventBus.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void render_return(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
        WorldRenderEvent.Post event = new WorldRenderEvent.Post(tickCounter.getTickDelta(true));
        KiwiClient.eventBus.post(event);
    }

    @Unique
    private static boolean SODIUM_INSTALLED = FabricLoader.getInstance().isModLoaded("sodium");

    @Redirect(method = "renderEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;render(Lnet/minecraft/entity/Entity;DDDFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    public <E extends Entity> void renderEntity_render(EntityRenderDispatcher dispatcher, E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        EntityRenderEvent.Single.Pre event = new EntityRenderEvent.Single.Pre(entity, matrices, vertexConsumers);
        KiwiClient.eventBus.post(event);

        if (!event.isCancelled()) {
            try {
                dispatcher.render(event.getEntity(), x, y, z, yaw, tickDelta, event.getMatrix(), SODIUM_INSTALLED ? vertexConsumers : event.getVertex(), light);
            } catch (Exception e) {
                System.out.println("Disabling Entity Rendering Mixin, another mod conflicting?");
                e.printStackTrace();
                SODIUM_INSTALLED = true;
            }
            //dispatcher.render(event.getEntity(), x, y, z, yaw, tickDelta, event.getMatrix(), event.getVertex(), light);
        }
    }
}
