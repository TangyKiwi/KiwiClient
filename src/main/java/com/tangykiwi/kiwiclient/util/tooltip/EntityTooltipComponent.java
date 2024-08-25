package com.tangykiwi.kiwiclient.util.tooltip;

import com.tangykiwi.kiwiclient.mixin.EntityAccessor;
import com.tangykiwi.kiwiclient.mixininterface.ITooltipData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
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
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

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
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(15, 2, 0);
        this.entity.setVelocity(1.f, 1.f, 1.f);
        this.renderEntity(matrices, x, y);
        matrices.pop();
    }

//    public static Optional<TooltipData> of(EntityType<?> type, NbtCompound itemNbt) {
//        var client = MinecraftClient.getInstance();
//        var entity = type.create(client.world);
//        if (entity != null) {
//            EntityType.loadFromEntityNbt(client.world, null, entity, itemNbt);
//            adjustEntity(entity, itemNbt);
//            ((EntityAccessor) entity).setInWater(true);
//            return Optional.of(new EntityTooltipComponent(entity));
//        }
//        return Optional.empty();
//    }

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
        Quaternionf quaternion = RotationAxis.POSITIVE_Z.rotationDegrees(180.f);
        Quaternionf quaternion2 = RotationAxis.POSITIVE_X.rotationDegrees(-10.f);
        hamiltonProduct(quaternion, quaternion2);
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

    public void hamiltonProduct(Quaternionf q, Quaternionf other) {
        float f = q.x();
        float g = q.y();
        float h = q.z();
        float i = q.w();
        float j = other.x();
        float k = other.y();
        float l = other.z();
        float m = other.w();
        q.x = (((i * j) + (f * m)) + (g * l)) - (h * k);
        q.y = ((i * k) - (f * l)) + (g * m) + (h * j);
        q.z = (((i * l) + (f * k)) - (g * j)) + (h * m);
        q.w = (((i * m) - (f * j)) - (g * k)) - (h * l);
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
                    tropicalFish.setVariant(TropicalFishEntity.getVariety(itemNbt.getInt(TropicalFishEntity.BUCKET_VARIANT_TAG_KEY)));
                }
            }
        }
    }
}
