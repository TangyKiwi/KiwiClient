package com.tangykiwi.kiwiclient.mixin;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.mixininterface.IEntityRenderState;
import com.tangykiwi.kiwiclient.module.render.Nametags;
import com.tangykiwi.kiwiclient.util.EntityUtils;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
	@Shadow
	protected EntityRenderDispatcher dispatcher;

	@Shadow
   	private TextRenderer textRenderer;

    // @WrapOperation(at = @At(value = "INVOKE",
	// 	target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;getSquaredDistanceToCamera(Lnet/minecraft/entity/Entity;)D"),
	// 	method = "updateRenderState(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/entity/state/EntityRenderState;F)V")
	// private double fakeSquaredDistanceToCamera(
	// 	EntityRenderDispatcher dispatcher, Entity entity,
	// 	Operation<Double> original,
	// 	@Share("actualDistanceSq") LocalDoubleRef actualDistanceSq)
	// {
	// 	actualDistanceSq.set(original.call(dispatcher, entity));
		
	// 	if(KiwiClient.moduleManager.getModule(Nametags.class).isEnabled())
	// 		return 0;
		
	// 	return actualDistanceSq.get();
	// }

    // @Inject(at = @At("TAIL"),
	// 	method = "updateRenderState(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/entity/state/EntityRenderState;F)V")
	// private void restoreSquaredDistanceToCamera(T entity, S state,
	// 	float tickDelta, CallbackInfo ci,
	// 	@Share("actualDistanceSq") LocalDoubleRef actualDistanceSq)
	// {
	// 	state.squaredDistanceToCamera = actualDistanceSq.get();
	// }

	@Inject(method = "render", at = @At("HEAD"))
	public void render(EntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		Nametags nametags = (Nametags) KiwiClient.moduleManager.getModule(Nametags.class);
		if (nametags.isEnabled()) {
			IEntityRenderState iState = (IEntityRenderState) state;
			if (iState.getLabel() != null) {
				customRenderLabel(state, iState.getLabel(), matrices, vertexConsumers, light);
			}
		}
	}

	private void customRenderLabel(EntityRenderState state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
      Vec3d vec3d = ((IEntityRenderState) state).getLabelPos();
      if (vec3d != null) {
         int i = "deadmau5".equals(text.getString()) ? -10 : 0;
         matrices.push();
         matrices.translate(vec3d.x, vec3d.y + 0.5, vec3d.z);
         matrices.multiply(this.dispatcher.getRotation());
         matrices.scale(0.025F, -0.025F, 0.025F);
         Matrix4f matrix4f = matrices.peek().getPositionMatrix();
         float f = (float)(-textRenderer.getWidth(text)) / 2.0F;
         int j = (int)(mc.options.getTextBackgroundOpacity(0.25F) * 255.0F) << 24;
         textRenderer.draw(text, f, (float)i, -2130706433, false, matrix4f, vertexConsumers, TextLayerType.SEE_THROUGH, j, light);
		 textRenderer.draw(text, f, (float)i, -1, false, matrix4f, vertexConsumers, TextLayerType.SEE_THROUGH, 0, LightmapTextureManager.applyEmission(light, 2));
         matrices.pop();
      }
   }

    @Inject(method = "updateRenderState", at = @At("HEAD"))
    private void modifyRenderState (Entity entity, EntityRenderState state, float tickDelta, CallbackInfo info) {
        Nametags nametags = (Nametags) KiwiClient.moduleManager.getModule(Nametags.class);
        if (nametags.isEnabled()) {
			IEntityRenderState iState = (IEntityRenderState) state;
			if (EntityUtils.isPlayer(entity) && nametags.getSetting("Players").asToggle().getValue()) {
				if (entity.getDisplayName() != null) iState.setLabel(entity.getDisplayName());
				else iState.setLabel(entity.getName());
				iState.setLabelPos(entity.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, entity.getLerpedYaw(tickDelta)));
			}
			else if (EntityUtils.isAnimal(entity) && nametags.getSetting("Animals").asToggle().getValue()) {
				if (entity.getDisplayName() != null) iState.setLabel(entity.getDisplayName());
				else iState.setLabel(entity.getName());
				iState.setLabelPos(entity.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, entity.getLerpedYaw(tickDelta)));
			}
			else if (EntityUtils.isMob(entity) && nametags.getSetting("Mobs").asToggle().getValue()) {
				if (entity.getDisplayName() != null) iState.setLabel(entity.getDisplayName());
				else iState.setLabel(entity.getName());
				iState.setLabelPos(entity.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, entity.getLerpedYaw(tickDelta)));
			}
			else if (entity instanceof ItemEntity && nametags.getSetting("Items").asToggle().getValue()) {
				if (entity.getDisplayName() != null) iState.setLabel(entity.getDisplayName());
				else iState.setLabel(entity.getName());
				iState.setLabelPos(entity.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, entity.getLerpedYaw(tickDelta)));
			}
			else {
				iState.setLabel(null);
				iState.setLabelPos(null);
			}
		}
    }
}
