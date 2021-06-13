package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.util.ItemEntityRotator;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemEntity.class)
public class ItemEntityMixin implements ItemEntityRotator {

    private Vec3d rotation = new Vec3d(0, 0, 0);

    @Override
    public Vec3d getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(Vec3d rotation) {
        this.rotation = rotation;
    }

    @Override
    public void addRotation(Vec3d rotation) {
        this.rotation.add(rotation);
    }

    @Override
    public void addRotation(double x, double y, double z) {
        this.rotation.add(x, y, z);
    }
}
