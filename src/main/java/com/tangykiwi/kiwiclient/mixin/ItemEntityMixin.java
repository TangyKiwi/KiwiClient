package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.util.ItemPhysicsExtension;
import net.minecraft.entity.*;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements ItemPhysicsExtension {

    @Shadow
    public abstract ItemStack getStack();

    @Shadow
    @Nullable
    public abstract UUID getThrower();

    @Shadow
    private int itemAge;

    @Unique
    private float rotation = -1;

    private ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
}
