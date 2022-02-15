package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.RenderItemEntityEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;
import com.tangykiwi.kiwiclient.util.IItemEntity;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class ItemPhysics extends Module {
    public ItemPhysics() {
        super("ItemPhysics", "Better item physics", KEY_UNBOUND, Category.RENDER,
                new SliderSetting("Roll", 0, 50, 0, 0),
                new SliderSetting("Pitch", 0, 50, 25, 0),
                new SliderSetting("Yaw", 0, 50, 25, 0));
    }

    private final HashMap<ItemEntity, Float> itemRotationsRoll = new HashMap<>();
    private final HashMap<ItemEntity, Float> prevItemRotationsRoll = new HashMap<>();

    private final HashMap<ItemEntity, Float> itemRotationsPitch = new HashMap<>();
    private final HashMap<ItemEntity, Float> prevItemRotationsPitch = new HashMap<>();

    private final HashMap<ItemEntity, Float> itemRotationsYaw = new HashMap<>();
    private final HashMap<ItemEntity, Float> prevItemRotationsYaw = new HashMap<>();

    private final HashMap<ItemEntity, Boolean> itemPitchNeg = new HashMap<>();
    private final HashMap<ItemEntity, Boolean> itemRollNeg = new HashMap<>();
    private final HashMap<ItemEntity, Boolean> itemYawNeg = new HashMap<>();

    @Subscribe
    @AllowConcurrentEvents
    public void onRenderItemEntity(RenderItemEntityEvent event) {
        ItemEntity itemEntity = event.itemEntity;
        MatrixStack matrixStack = event.matrixStack;
        float g = event.tickDelta;
        float n = itemEntity.getRotation(g);

        if (!prevItemRotationsRoll.containsKey(itemEntity))
            return;
        matrixStack.translate(0, itemEntity.getHeight() / 1.5f, 0);

        BakedModel bakedModel = mc.getItemRenderer().getModel(itemEntity.getStack(), itemEntity.world, null, itemEntity.getId());
        float roll = MathHelper.lerp(mc.getTickDelta(), prevItemRotationsRoll.get(itemEntity), itemRotationsRoll.get(itemEntity));
        float pitch = MathHelper.lerp(mc.getTickDelta(), prevItemRotationsPitch.get(itemEntity), itemRotationsPitch.get(itemEntity));
        float yaw = MathHelper.lerp(mc.getTickDelta(), prevItemRotationsYaw.get(itemEntity), itemRotationsYaw.get(itemEntity));

        if (itemEntity.isOnGround())
            matrixStack.translate(0, bakedModel.hasDepth() ? -0.04 : -0.151f, 0);

        matrixStack.multiply(new Quaternion(new Vec3f(itemPitchNeg.get(itemEntity) ? -1 : 1, 0, 0), pitch, true));
        matrixStack.multiply(new Quaternion(new Vec3f(0, 0, itemRollNeg.get(itemEntity) ? -1 : 1), roll, true));
        matrixStack.multiply(new Quaternion(new Vec3f(0, itemYawNeg.get(itemEntity) ? -1 : 1, 0), yaw, true));

        matrixStack.translate(0, -(itemEntity.getHeight() / 1.5f), 0);

        matrixStack.multiply(Vec3f.NEGATIVE_Y.getRadialQuaternion(n));
        float l = MathHelper.sin(((float)itemEntity.getItemAge() + g) / 10.0F + itemEntity.uniqueOffset) * 0.1F + 0.1F;
        float m = bakedModel.getTransformation().getTransformation(ModelTransformation.Mode.GROUND).scale.getY();
        matrixStack.translate(0.0D, -(l + 0.25F * m), 0.0D);
    }
    
    @Subscribe
    @AllowConcurrentEvents
    public void onTick(TickEvent event) {
        if (mc.world != null)
            mc.world.getEntities().forEach(entity -> {
                if (entity instanceof ItemEntity itemEntity) {
                    if (!itemRotationsRoll.containsKey(itemEntity)) {
                        Random r = new Random();
                        float roll = r.nextFloat() * 360;
                        float yaw = r.nextFloat() * 360;
                        float pitch = r.nextFloat() * 360;
                        itemRotationsRoll.put(itemEntity, roll);
                        prevItemRotationsRoll.put(itemEntity, roll);
                        itemRotationsPitch.put(itemEntity, yaw);
                        prevItemRotationsPitch.put(itemEntity, yaw);
                        itemRotationsYaw.put(itemEntity, pitch);
                        prevItemRotationsYaw.put(itemEntity, pitch);
                        itemPitchNeg.put(itemEntity, r.nextBoolean());
                        itemRollNeg.put(itemEntity, r.nextBoolean());
                        itemYawNeg.put(itemEntity, r.nextBoolean());
                    }
                    prevItemRotationsRoll.replace(itemEntity, itemRotationsRoll.get(itemEntity));
                    if (!itemEntity.isOnGround()) {
                        itemRotationsRoll.replace(itemEntity, itemRotationsRoll.get(itemEntity) + getSetting(0).asSlider().getValueFloat());
                    }
                    prevItemRotationsPitch.replace(itemEntity, itemRotationsPitch.get(itemEntity));
                    if (!itemEntity.isOnGround()) {
                        itemRotationsPitch.replace(itemEntity, itemRotationsPitch.get(itemEntity) + getSetting(1).asSlider().getValueFloat());
                    } else
                        itemRotationsPitch.replace(itemEntity, 90.f);
                    prevItemRotationsYaw.replace(itemEntity, itemRotationsYaw.get(itemEntity));
                    if (!itemEntity.isOnGround()) {
                        itemRotationsYaw.replace(itemEntity, itemRotationsYaw.get(itemEntity) + getSetting(2).asSlider().getValueFloat());
                    } else
                        itemRotationsYaw.replace(itemEntity, 0.f);
                }
            });
        itemRotationsRoll.keySet().removeIf(Objects::isNull);
        prevItemRotationsRoll.keySet().removeIf(Objects::isNull);
        itemRotationsPitch.keySet().removeIf(Objects::isNull);
        prevItemRotationsPitch.keySet().removeIf(Objects::isNull);
        itemRotationsYaw.keySet().removeIf(Objects::isNull);
        prevItemRotationsYaw.keySet().removeIf(Objects::isNull);
        itemPitchNeg.keySet().removeIf(Objects::isNull);
        itemRollNeg.keySet().removeIf(Objects::isNull);
        itemYawNeg.keySet().removeIf(Objects::isNull);
    }
}
