package com.tangykiwi.kiwiclient.util.cosmetics;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.other.WeaponMaster;
import net.minecraft.util.math.Vec3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Arm;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.TridentItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.model.json.ModelTransformation;

public class HumanoidItemLayer<T extends LivingEntity, M extends BipedEntityModel<T> & ModelWithArms> extends FeatureRenderer<T, M> {
    public HumanoidItemLayer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if(!KiwiClient.moduleManager.getModule(WeaponMaster.class).isEnabled()) return;

        matrices.push();
        if (this.getContextModel().child) {
            matrices.translate(0.0D, 0.75D, 0.0D);
            matrices.scale(0.5F, 0.5F, 0.5F);
        }

        this.renderPlayerWithItems(entity, ((PlayerEntity)entity).getInventory().main.get(0), ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND, Arm.RIGHT, matrices, vertexConsumers, light);
        matrices.pop();
    }

    private void renderPlayerWithItems(T entity, ItemStack itemStack, ModelTransformation.Mode thirdPersonRightHand, Arm right, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (entity instanceof ClientPlayerEntity) {
            this.renderWithItems(entity, itemStack, right, matrices, vertexConsumers, light);
        }

    }

    private void renderWithItems(T p_174518_, ItemStack p_174519_, Arm p_174520_, MatrixStack p_174521_, VertexConsumerProvider p_174522_, int p_174523_) {
        int shieldAt = -1;

        int bannerAt;
        for(bannerAt = 0; bannerAt < 9; ++bannerAt) {
            if (((PlayerEntity)p_174518_).getInventory().main.get(bannerAt).getItem() instanceof ShieldItem) {
                shieldAt = bannerAt;
                break;
            }
        }

        bannerAt = -1;

        int slot;
        for(slot = 0; slot < 9; ++slot) {
            if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof BannerItem) {
                bannerAt = slot;
                break;
            }
        }

        if (bannerAt != -1 && ((PlayerEntity)p_174518_).getInventory().selectedSlot != bannerAt && KiwiClient.moduleManager.getModule(WeaponMaster.class).getSetting(0).asToggle().state) {
            p_174521_.push();
            this.copyModelPartRotation(p_174521_, this.getContextModel().head, 0.01F, 1.0F);
            p_174521_.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            p_174521_.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
            p_174521_.translate(0.0D, 0.6D, -0.3D);
            p_174521_.scale(0.75F, 0.75F, 0.75F);
            MinecraftClient.getInstance().getHeldItemRenderer().renderItem(p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(bannerAt), ModelTransformation.Mode.NONE, false, p_174521_, p_174522_, p_174523_);
            p_174521_.pop();
        }

        p_174521_.push();
        if (shieldAt != -1 && ((PlayerEntity)p_174518_).getInventory().selectedSlot != shieldAt && KiwiClient.moduleManager.getModule(WeaponMaster.class).getSetting(1).asToggle().state) {
            p_174521_.push();
            if (p_174518_.hasStackEquipped(EquipmentSlot.CHEST)) {
                p_174521_.translate(0.0D, 0.0D, 0.075D);
            }

            this.copyModelPartRotation(p_174521_, this.getContextModel().body, 1.0F, 1.0F);
            p_174521_.translate(0.30000001192092896D, 0.7D, 0.5D);
            p_174521_.scale(0.6F, 0.6F, 0.6F);
            MinecraftClient.getInstance().getHeldItemRenderer().renderItem(p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(shieldAt), ModelTransformation.Mode.NONE, false, p_174521_, p_174522_, p_174523_);
            p_174521_.pop();
        }

        slot = 0;
        if (!((PlayerEntity)p_174518_).getInventory().main.get(slot).isEmpty() && ((PlayerEntity)p_174518_).getInventory().selectedSlot != slot && shieldAt != slot && bannerAt != slot && KiwiClient.moduleManager.getModule(WeaponMaster.class).getSetting(2).asToggle().state) {
            p_174521_.push();
            if (p_174518_.hasStackEquipped(EquipmentSlot.CHEST)) {
                p_174521_.translate(0.0D, 0.0D, 0.075D);
            }

            this.copyModelPartRotation(p_174521_, this.getContextModel().body, 1.0F, 1.0F);
            if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof ToolItem) {
                this.renderTieredItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof MiningToolItem) {
                this.renderDiggerItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof BowItem) {
                this.renderBowItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof CrossbowItem) {
                this.renderCrossbowItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof TridentItem) {
                this.renderTridentItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            }

            p_174521_.pop();
        }

        slot = slot + 1;
        if (!((PlayerEntity)p_174518_).getInventory().main.get(slot).isEmpty() && ((PlayerEntity)p_174518_).getInventory().selectedSlot != slot && shieldAt != slot && bannerAt != slot && KiwiClient.moduleManager.getModule(WeaponMaster.class).getSetting(3).asToggle().state) {
            p_174521_.push();
            if (p_174518_.hasStackEquipped(EquipmentSlot.CHEST)) {
                p_174521_.translate(0.0D, 0.0D, 0.075D);
            }

            this.copyModelPartRotation(p_174521_, this.getContextModel().body, 1.0F, 1.0F);
            if (((PlayerEntity)p_174518_).getInventory().main.get(slot - 1).isEmpty() || ((PlayerEntity)p_174518_).getInventory().selectedSlot == slot - 1) {
                p_174521_.translate(0.0D, 0.0D, -0.05D);
            }

            p_174521_.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            p_174521_.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(0.0F));
            p_174521_.translate(0.0D, 0.0D, -0.375D);
            if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof ToolItem) {
                this.renderTieredItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof MiningToolItem) {
                this.renderDiggerItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof BowItem) {
                this.renderBowItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof CrossbowItem) {
                this.renderCrossbowItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof TridentItem) {
                this.renderTridentItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            }

            p_174521_.pop();
        }

        ++slot;
        if (!((PlayerEntity)p_174518_).getInventory().main.get(slot).isEmpty() && ((PlayerEntity)p_174518_).getInventory().selectedSlot != slot && shieldAt != slot && bannerAt != slot && KiwiClient.moduleManager.getModule(WeaponMaster.class).getSetting(4).asToggle().state) {
            p_174521_.push();
            if (p_174518_.hasStackEquipped(EquipmentSlot.CHEST) || p_174518_.hasStackEquipped(EquipmentSlot.LEGS)) {
                p_174521_.translate(-0.03500000014901161D, 0.0D, 0.0D);
            }

            this.copyModelPartRotation(p_174521_, this.getContextModel().rightLeg, 0.5F, 0.15F);
            p_174521_.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
            p_174521_.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(0.0F));
            p_174521_.translate(0.125D, -0.2D, -0.005D);
            if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof ToolItem) {
                this.renderTieredItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof MiningToolItem) {
                this.renderDiggerItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof BowItem) {
                this.renderBowItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof CrossbowItem) {
                this.renderCrossbowItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof TridentItem) {
                this.renderTridentItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            }

            p_174521_.pop();
        }

        ++slot;
        if (!((PlayerEntity)p_174518_).getInventory().main.get(slot).isEmpty() && ((PlayerEntity)p_174518_).getInventory().selectedSlot != slot && shieldAt != slot && bannerAt != slot && KiwiClient.moduleManager.getModule(WeaponMaster.class).getSetting(5).asToggle().state) {
            p_174521_.push();
            if (p_174518_.hasStackEquipped(EquipmentSlot.CHEST) || p_174518_.hasStackEquipped(EquipmentSlot.LEGS)) {
                p_174521_.translate(0.03500000014901161D, 0.0D, 0.0D);
            }

            this.copyModelPartRotation(p_174521_, this.getContextModel().leftLeg, 0.5F, 0.15F);
            p_174521_.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
            p_174521_.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(0.0F));
            p_174521_.translate(0.125D, -0.2D, -0.315D);
            if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof ToolItem) {
                this.renderTieredItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof MiningToolItem) {
                this.renderDiggerItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof BowItem) {
                this.renderBowItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof CrossbowItem) {
                this.renderCrossbowItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof TridentItem) {
                this.renderTridentItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            }

            p_174521_.pop();
        }

        ++slot;
        if (!((PlayerEntity)p_174518_).getInventory().main.get(slot).isEmpty() && ((PlayerEntity)p_174518_).getInventory().selectedSlot != slot && shieldAt != slot && bannerAt != slot && KiwiClient.moduleManager.getModule(WeaponMaster.class).getSetting(6).asToggle().state) {
            p_174521_.push();
            if (p_174518_.hasStackEquipped(EquipmentSlot.CHEST) || p_174518_.hasStackEquipped(EquipmentSlot.LEGS)) {
                p_174521_.translate(-0.03500000014901161D, 0.0D, 0.0D);
            }

            this.copyModelPartRotation(p_174521_, this.getContextModel().rightLeg, 0.5F, 0.15F);
            if (((PlayerEntity)p_174518_).getInventory().main.get(2).isEmpty() || ((PlayerEntity)p_174518_).getInventory().selectedSlot == 2 || !KiwiClient.moduleManager.getModule(WeaponMaster.class).getSetting(4).asToggle().state) {
                p_174521_.translate(0.045D, 0.0D, 0.0D);
            }

            p_174521_.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
            p_174521_.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-30.0F));
            p_174521_.translate(0.125D, -0.2D, 0.04D);
            if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof ToolItem) {
                this.renderTieredItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof MiningToolItem) {
                this.renderDiggerItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof BowItem) {
                this.renderBowItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof CrossbowItem) {
                this.renderCrossbowItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof TridentItem) {
                this.renderTridentItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            }

            p_174521_.pop();
        }

        ++slot;
        if (!((PlayerEntity)p_174518_).getInventory().main.get(slot).isEmpty() && ((PlayerEntity)p_174518_).getInventory().selectedSlot != slot && shieldAt != slot && bannerAt != slot && KiwiClient.moduleManager.getModule(WeaponMaster.class).getSetting(7).asToggle().state) {
            p_174521_.push();
            if (p_174518_.hasStackEquipped(EquipmentSlot.CHEST) || p_174518_.hasStackEquipped(EquipmentSlot.LEGS)) {
                p_174521_.translate(0.03500000014901161D, 0.0D, 0.0D);
            }

            this.copyModelPartRotation(p_174521_, this.getContextModel().leftLeg, 0.5F, 0.15F);
            if (((PlayerEntity)p_174518_).getInventory().main.get(3).isEmpty() || ((PlayerEntity)p_174518_).getInventory().selectedSlot == 3 || !KiwiClient.moduleManager.getModule(WeaponMaster.class).getSetting(5).asToggle().state) {
                p_174521_.translate(-0.045D, 0.0D, 0.0D);
            }

            p_174521_.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
            p_174521_.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-30.0F));
            p_174521_.translate(0.125D, -0.2D, -0.36D);
            if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof ToolItem) {
                this.renderTieredItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof MiningToolItem) {
                this.renderDiggerItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof BowItem) {
                this.renderBowItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof CrossbowItem) {
                this.renderCrossbowItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof TridentItem) {
                this.renderTridentItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            }

            p_174521_.pop();
        }

        ++slot;
        if (!((PlayerEntity)p_174518_).getInventory().main.get(slot).isEmpty() && ((PlayerEntity)p_174518_).getInventory().selectedSlot != slot && shieldAt != slot && bannerAt != slot && KiwiClient.moduleManager.getModule(WeaponMaster.class).getSetting(8).asToggle().state) {
            p_174521_.push();
            if (p_174518_.hasStackEquipped(EquipmentSlot.CHEST)) {
                p_174521_.translate(0.0D, 0.0D, 0.075D);
            }

            this.copyModelPartRotation(p_174521_, this.getContextModel().body, 1.0F, 1.0F);
            p_174521_.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(0.0F));
            p_174521_.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-35.0F));
            p_174521_.translate(-0.3D, 0.2D, -0.005D);
            if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof ToolItem) {
                this.renderTieredItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof MiningToolItem) {
                this.renderDiggerItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof BowItem) {
                this.renderBowItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof CrossbowItem) {
                this.renderCrossbowItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof TridentItem) {
                this.renderTridentItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            }

            p_174521_.pop();
        }

        ++slot;
        if (!((PlayerEntity)p_174518_).getInventory().main.get(slot).isEmpty() && ((PlayerEntity)p_174518_).getInventory().selectedSlot != slot && shieldAt != slot && bannerAt != slot && KiwiClient.moduleManager.getModule(WeaponMaster.class).getSetting(9).asToggle().state) {
            p_174521_.push();
            if (p_174518_.hasStackEquipped(EquipmentSlot.CHEST)) {
                p_174521_.translate(0.0D, 0.0D, 0.1D);
            }

            this.copyModelPartRotation(p_174521_, this.getContextModel().body, 1.0F, 1.0F);
            p_174521_.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(0.0F));
            p_174521_.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(45.0F));
            p_174521_.translate(0.25D, -0.1D, 0.175D);
            if (shieldAt != -1 && ((PlayerEntity)p_174518_).getInventory().selectedSlot != shieldAt) {
                p_174521_.translate(0.0D, 0.0D, -0.0D);
            } else {
                if (((PlayerEntity)p_174518_).getInventory().main.get(1).isEmpty() || ((PlayerEntity)p_174518_).getInventory().selectedSlot == 1 || ((PlayerEntity)p_174518_).getInventory().main.get(0).isEmpty() || ((PlayerEntity)p_174518_).getInventory().selectedSlot == 0) {
                    p_174521_.translate(0.0D, 0.0D, -0.046D);
                }

                p_174521_.translate(0.0D, 0.0D, -0.05D);
            }

            if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof ToolItem) {
                this.renderTieredItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof MiningToolItem) {
                this.renderDiggerItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof BowItem) {
                this.renderBowItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof CrossbowItem) {
                this.renderCrossbowItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            } else if (((PlayerEntity)p_174518_).getInventory().main.get(slot).getItem() instanceof TridentItem) {
                this.renderTridentItem(slot, p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), p_174520_, p_174521_, p_174522_, p_174523_);
            }

            p_174521_.pop();
        }

        p_174521_.pop();
    }

    private void renderTridentItem(int slot, T p_174518_, ItemStack itemStack, Arm p_174520_, MatrixStack p_174521_, VertexConsumerProvider p_174522_, int p_174523_) {
        p_174521_.push();
        p_174521_.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(140.0F));
        p_174521_.translate(0.699999988079071D, 0.9D, 0.61D);
        p_174521_.scale(0.9F, 0.9F, 0.9F);
        MinecraftClient.getInstance().getHeldItemRenderer().renderItem(p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), ModelTransformation.Mode.NONE, false, p_174521_, p_174522_, p_174523_);
        p_174521_.pop();
    }

    private void renderCrossbowItem(int slot, T p_174518_, ItemStack itemStack, Arm p_174520_, MatrixStack p_174521_, VertexConsumerProvider p_174522_, int p_174523_) {
        p_174521_.push();
        p_174521_.translate(-0.05D, 0.35D, 0.16D);
        p_174521_.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
        p_174521_.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(270.0F));
        p_174521_.scale(0.8F, -0.8F, -0.8F);
        MinecraftClient.getInstance().getHeldItemRenderer().renderItem(p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), ModelTransformation.Mode.NONE, false, p_174521_, p_174522_, p_174523_);
        p_174521_.pop();
    }

    private void renderBowItem(int slot, T p_174518_, ItemStack itemStack, Arm p_174520_, MatrixStack p_174521_, VertexConsumerProvider p_174522_, int p_174523_) {
        p_174521_.push();
        p_174521_.translate(0.0D, 0.35D, 0.16D);
        p_174521_.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
        p_174521_.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
        p_174521_.scale(0.8F, -0.8F, -0.8F);
        MinecraftClient.getInstance().getHeldItemRenderer().renderItem(p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), ModelTransformation.Mode.NONE, false, p_174521_, p_174522_, p_174523_);
        p_174521_.pop();
    }

    private void renderDiggerItem(int slot, T p_174518_, ItemStack itemStack, Arm p_174520_, MatrixStack p_174521_, VertexConsumerProvider p_174522_, int p_174523_) {
        p_174521_.push();
        p_174521_.translate(-0.05D, 0.35D, 0.16D);
        p_174521_.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(0.0F));
        p_174521_.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90.0F));
        p_174521_.scale(0.8F, -0.8F, -0.8F);
        MinecraftClient.getInstance().getHeldItemRenderer().renderItem(p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), ModelTransformation.Mode.NONE, false, p_174521_, p_174522_, p_174523_);
        p_174521_.pop();
    }

    private void renderTieredItem(int slot, T p_174518_, ItemStack itemStack, Arm p_174520_, MatrixStack p_174521_, VertexConsumerProvider p_174522_, int p_174523_) {
        p_174521_.push();
        p_174521_.translate(-0.05D, 0.3D, 0.16D);
        p_174521_.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
        p_174521_.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
        p_174521_.scale(0.8F, -0.8F, -0.8F);
        MinecraftClient.getInstance().getHeldItemRenderer().renderItem(p_174518_, ((PlayerEntity)p_174518_).getInventory().main.get(slot), ModelTransformation.Mode.NONE, false, p_174521_, p_174522_, p_174523_);
        p_174521_.pop();
    }

    public void copyModelPartRotation(MatrixStack ps, ModelPart modelpartx, float multiplier, float maxRotateChanger) {
        float fx = modelpartx.pitch;
        modelpartx.pitch = MathHelper.clamp(modelpartx.pitch, -3.1415927F / (6.0F * multiplier), 3.1415927F / (2.0F * multiplier)) * maxRotateChanger;
        modelpartx.rotate(ps);
        modelpartx.pitch = fx;
    }
}

