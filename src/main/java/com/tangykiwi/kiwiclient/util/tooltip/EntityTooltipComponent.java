package com.tangykiwi.kiwiclient.util.tooltip;

import com.tangykiwi.kiwiclient.mixin.EntityAccessor;
import com.tangykiwi.kiwiclient.mixininterface.ITooltipData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

import java.util.Optional;

public class EntityTooltipComponent implements ITooltipData, TooltipComponent {
    protected final Entity entity;

    public EntityTooltipComponent(Entity entity) {
        this.entity = entity;
    }

    @Override
    public TooltipComponent getComponent() {
        return this;
    }

    @Override
    public int getHeight() {
        return 24;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return 60;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
        matrices.push();
        matrices.translate(15, 2, z);
        this.entity.setVelocity(1.f, 1.f, 1.f);
        this.renderEntity(matrices, x, y);
        matrices.pop();
    }

    public static Optional<TooltipData> of(EntityType<?> type, NbtCompound itemNbt) {
        var client = MinecraftClient.getInstance();
        var entity = type.create(client.world);
        if (entity != null) {
            EntityType.loadFromEntityNbt(client.world, null, entity, itemNbt);
            adjustEntity(entity, itemNbt);
            ((EntityAccessor) entity).setInWater(true);
            return Optional.of(new EntityTooltipComponent(entity));
        }
        return Optional.empty();
    }

    protected void renderEntity(MatrixStack matrices, int x, int y) {
        if (mc.player == null) return;
        float size = 24;
        if (Math.max(entity.getWidth(), entity.getHeight()) > 1.0) {
            size /= Math.max(entity.getWidth(), entity.getHeight());
        }
        DiffuseLighting.disableGuiDepthLighting();
        matrices.push();
        int yOffset = 16;

        if (entity instanceof SquidEntity) {
            size = 16;
            yOffset = 2;
        }

        matrices.translate(x + 10, y + yOffset, 1050);
        matrices.scale(1f, 1f, -1);
        matrices.translate(0, 0, 1000);
        matrices.scale(size, size, size);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.f);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(-10.f);
        quaternion.hamiltonProduct(quaternion2);
        matrices.multiply(quaternion);
        setupAngles();
        EntityRenderDispatcher entityRenderDispatcher = mc.getEntityRenderDispatcher();
        quaternion2.conjugate();
        entityRenderDispatcher.setRotation(quaternion2);
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        entity.age = mc.player.age;
        entity.setCustomNameVisible(false);
        entityRenderDispatcher.render(entity, 0, 0, 0, 0.f, 1.f, matrices, immediate, 15728880);
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        matrices.pop();
        DiffuseLighting.enableGuiDepthLighting();
    }

    protected void setupAngles() {
        float yaw = (float) (((System.currentTimeMillis() / 10)) % 360);
        entity.setYaw(yaw);
        entity.setHeadYaw(yaw);
        entity.setPitch(0.f);
        if (entity instanceof LivingEntity) {
            if (entity instanceof GoatEntity) ((LivingEntity) entity).headYaw = yaw;
            ((LivingEntity) entity).bodyYaw = yaw;
        }
    }

    protected static void adjustEntity(Entity entity, NbtCompound itemNbt) {
        if (entity instanceof Bucketable bucketable) {
            bucketable.copyDataFromNbt(itemNbt);
            if (entity instanceof PufferfishEntity pufferfish) {
                pufferfish.setPuffState(2);
            } else if (entity instanceof TropicalFishEntity tropicalFish) {
                if (itemNbt.contains("BucketVariantTag", NbtElement.INT_TYPE)) {
                    tropicalFish.setVariant(itemNbt.getInt("BucketVariantTag"));
                }
            }
        }
    }
}
