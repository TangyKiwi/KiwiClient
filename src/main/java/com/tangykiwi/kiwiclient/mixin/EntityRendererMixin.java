package com.tangykiwi.kiwiclient.mixin;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.mixininterface.IEntityRenderState;
import com.tangykiwi.kiwiclient.module.render.Nametags;
import com.tangykiwi.kiwiclient.util.EntityUtils;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

// @Mixin(EntityRenderer.class)
// public class EntityRendererMixin {
// 	@Shadow
// 	protected EntityRenderManager dispatcher;

// 	@Shadow
//    	private TextRenderer textRenderer;

// 	@Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
// 	public void renderLabelIfPresent(EntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState, CallbackInfo ci) {
// 		Nametags nametags = (Nametags) KiwiClient.moduleManager.getModule(Nametags.class);
// 		if (nametags.isEnabled()) {
// 			IEntityRenderState iState = (IEntityRenderState) state;
// 			if (iState.getLabel() != null) {
// 				ci.cancel();
// 			}
// 		}
// 	}

// 	// @Inject(method = "render", at = @At("HEAD"))
// 	// public void render(EntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState, CallbackInfo ci) {
// 	// 	Nametags nametags = (Nametags) KiwiClient.moduleManager.getModule(Nametags.class);
// 	// 	if (nametags.isEnabled()) {
// 	// 		IEntityRenderState iState = (IEntityRenderState) state;
// 	// 		if (iState.getLabel() != null) {
// 	// 			customRenderLabel(state, iState.getLabel(), matrices);
// 	// 		}
// 	// 	}
// 	// }

// 	private void customRenderLabel(EntityRenderState state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
// 		IEntityRenderState iState = (IEntityRenderState) state;
//     	Vec3d vec3d = iState.getLabelPos();
//       	if (vec3d != null) {
// 			int i = "deadmau5".equals(text.getString()) ? -10 : 0;
// 			matrices.push();
// 			matrices.translate(vec3d.x, vec3d.y + 0.5, vec3d.z);
// 			matrices.multiply(dispatcher.camera.getRotation());
// 			double d = Math.sqrt(state.squaredDistanceToCamera);
// 			float scale = (float) Math.max(1, d / 10);
// 			matrices.push();
// 			matrices.scale(scale * 0.025F, -scale * 0.025F, scale * 0.025F);
// 			Matrix4f matrix4f = matrices.peek().getPositionMatrix();
// 			float f = (float)(-textRenderer.getWidth(text)) / 2.0F;
// 			int j = (int)(mc.options.getTextBackgroundOpacity(0.25F) * 255.0F) << 24;
// 			textRenderer.draw(text, f, (float)i, -2130706433, false, matrix4f, vertexConsumers, TextLayerType.SEE_THROUGH, j, light);
// 			textRenderer.draw(text, f, (float)i, -1, false, matrix4f, vertexConsumers, TextLayerType.SEE_THROUGH, 0, LightmapTextureManager.applyEmission(light, 2));
// 			matrices.pop();
// 			matrices.pop();
//       	}
//    }

//     @Inject(method = "updateRenderState", at = @At("HEAD"))
//     private void modifyRenderState (Entity entity, EntityRenderState state, float tickDelta, CallbackInfo info) {
//         Nametags nametags = (Nametags) KiwiClient.moduleManager.getModule(Nametags.class);
//         if (nametags.isEnabled()) {
// 			IEntityRenderState iState = (IEntityRenderState) state;
// 			if (EntityUtils.isOtherServerPlayer(entity) && nametags.getSetting("Players").asToggle().getValue()) {
// 				setLabelAndPos(entity, state, tickDelta);
// 			}
// 			else if (EntityUtils.isAnimal(entity) && nametags.getSetting("Animals").asToggle().getValue()) {
// 				setLabelAndPos(entity, state, tickDelta);
// 			}
// 			else if (EntityUtils.isMob(entity) && nametags.getSetting("Mobs").asToggle().getValue()) {
// 				setLabelAndPos(entity, state, tickDelta);
// 			}
// 			else if (entity instanceof ItemEntity && nametags.getSetting("Items").asToggle().getValue()) {
// 				setLabelAndPos(entity, state, tickDelta);
// 			}
// 			else {
// 				iState.setLabel(null);
// 				iState.setLabelPos(null);
// 			}
// 		}
//     }

// 	private void setLabelAndPos(Entity entity, EntityRenderState state, float tickDelta) {
// 		IEntityRenderState iState = (IEntityRenderState) state;
// 		MutableText label = entity.getDisplayName() != null ? Text.literal(entity.getDisplayName().getString()) : Text.literal(entity.getName().getString());
// 		if (entity instanceof LivingEntity livingEntity) {
// 			double health = livingEntity.getHealth();
// 			label.append(Text.literal(" ").append(String.format("%.1f", health)).formatted(getColor(health)));
// 		} else if (entity instanceof ItemEntity itemEntity) {
// 			int count = itemEntity.getStack().getCount();
// 			label.append(Text.literal(" [x").append(Integer.toString(count)).append("]"));
// 		}
// 		iState.setEntity(entity);
// 		iState.setLabel(label);
// 		iState.setLabelPos(entity.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, entity.getLerpedYaw(tickDelta)));
// 	}

// 	private Formatting getColor(double health)
// 	{
// 		if(health <= 5)
// 			return Formatting.DARK_RED;
		
// 		if(health <= 10)
// 			return Formatting.GOLD;
		
// 		if(health <= 15)
// 			return Formatting.YELLOW;
		
// 		return Formatting.GREEN;
// 	}

// 	private ItemStack getItem(LivingEntity entity, int index) {
//         return switch (index) {
//             case 0 -> entity.getMainHandStack();
//             case 1 -> entity.getEquippedStack(EquipmentSlot.HEAD);
//             case 2 -> entity.getEquippedStack(EquipmentSlot.CHEST);
//             case 3 -> entity.getEquippedStack(EquipmentSlot.LEGS);
//             case 4 -> entity.getEquippedStack(EquipmentSlot.FEET);
//             case 5 -> entity.getOffHandStack();
//             default -> ItemStack.EMPTY;
//         };
//     }
// }
