package com.tangykiwi.kiwiclient.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraAccessor {
    @Invoker("move")
    void moveCameraBy(float f, float g, float h);

    @Invoker("setPosition")
    void setCameraPos(Vec3 vec);

    @Invoker("setRotation")
    void setCameraRotation(float yaw, float pitch);
}