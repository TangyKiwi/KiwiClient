package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.mixininterface.IVec3d;
import com.tangykiwi.kiwiclient.modules.render.Freecam;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({Camera.class})
public abstract class CameraMixin {
    @Shadow
    private Vec3d pos;

    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Shadow private boolean thirdPerson;

    @Shadow protected abstract double clipToSpace(double desiredCameraDistance);

    @Inject(
            method = {"update"},
            at = {@At("TAIL")}
    )
    private void update(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        Freecam freecam = (Freecam) KiwiClient.moduleManager.getModule(Freecam.class);
        if (freecam.isEnabled()) {
            ((IVec3d) this.pos).set(freecam.getX(tickDelta), freecam.getY(tickDelta), freecam.getZ(tickDelta));
            this.setRotation((float)freecam.getYaw(tickDelta), (float)freecam.getPitch(tickDelta));
            this.thirdPerson = true;
        }
    }

    @ModifyArgs(
            method = {"update"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;moveBy(DDD)V",
                    ordinal = 0
            )
    )
    private void modifyCameraDistance(Args args) {
        if (KiwiClient.moduleManager.getModule(Freecam.class).isEnabled()) {
            args.set(0, -this.clipToSpace(0.0D));
        }
    }
}
