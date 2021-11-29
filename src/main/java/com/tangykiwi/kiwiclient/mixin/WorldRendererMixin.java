package com.tangykiwi.kiwiclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.EntityRenderEvent;
import com.tangykiwi.kiwiclient.event.WorldRenderEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.profiler.Profiler;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @ModifyConstant(method = "render", require = 1, constant = @Constant(intValue = 0),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;beginWrite(Z)V", ordinal = 1),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilderStorage;getEntityVertexConsumers()Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;")))
    public int render_modifyBoolean(int old) {
        return 1;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"))
    private void render_swap(Profiler profiler, String string) {
        if (string.equals("entities")) {
            KiwiClient.eventBus.post(new EntityRenderEvent.PreAll());
        } else if (string.equals("blockentities")) {
            KiwiClient.eventBus.post(new EntityRenderEvent.PostAll());
            //KiwiClient.eventBus.post(new EventBlockEntityRender.PreAll());
        } else if (string.equals("blockentities")) {
            KiwiClient.eventBus.post(new EntityRenderEvent.PostAll());
            //KiwiClient.eventBus.post(new EventBlockEntityRender.PreAll());
        } else if (string.equals("destroyProgress")) {
            //KiwiClient.eventBus.post(new EventBlockEntityRender.PostAll());
        }
    }

    @Redirect(method = "renderEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;render(Lnet/minecraft/entity/Entity;DDDFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    public <E extends Entity> void renderEntity_render(EntityRenderDispatcher dispatcher, E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        EntityRenderEvent.Single.Pre event = new EntityRenderEvent.Single.Pre(entity, matrices, vertexConsumers);
        KiwiClient.eventBus.post(event);

        if (!event.isCancelled()) {
            dispatcher.render(event.getEntity(), x, y, z, yaw, tickDelta, event.getMatrix(), event.getVertex(), light);
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void render_return(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
                               LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
        WorldRenderEvent.Post event = new WorldRenderEvent.Post(tickDelta, matrices);
        KiwiClient.eventBus.post(event);
    }
}
