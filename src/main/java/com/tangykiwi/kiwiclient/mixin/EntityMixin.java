package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.player.Freecam;
import com.tangykiwi.kiwiclient.util.CameraEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public float yaw;
    @Shadow public float pitch;
    @Shadow public float prevYaw;
    @Shadow public float prevPitch;

    private double forcedPitch;
    private double forcedYaw;

    @Shadow public abstract net.minecraft.util.math.Vec3d getVelocity();
    @Shadow public abstract void setVelocity(net.minecraft.util.math.Vec3d velocity);

    @Inject(method = "updateVelocity", at = @At("HEAD"), cancellable = true)
    private void moreAccurateMoveRelative(float float_1, net.minecraft.util.math.Vec3d motion, CallbackInfo ci)
    {
        if ((Object) this instanceof net.minecraft.client.network.ClientPlayerEntity)
        {
            if (KiwiClient.moduleManager.getModule(Freecam.class).isEnabled())
            {
                CameraEntity camera = CameraEntity.getCamera();

                if (camera != null)
                {
                    this.setVelocity(this.getVelocity().multiply(1D, 0D, 1D));
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void overrideYaw(double yawChange, double pitchChange, CallbackInfo ci)
    {
        if ((Object) this instanceof net.minecraft.client.network.ClientPlayerEntity)
        {
            if (KiwiClient.moduleManager.getModule(Freecam.class).isEnabled())
            {
                this.yaw = this.prevYaw;
                this.pitch = this.prevPitch;

                this.updateCustomRotations(yawChange, pitchChange, true, true, 90);

                CameraEntity camera = CameraEntity.getCamera();

                if (camera != null)
                {
                    camera.setRotations((float) this.forcedYaw, (float) this.forcedPitch);
                }

                ci.cancel();

                return;
            }

            // Update the internal rotations while no locking features are enabled
            // They will then be used as the forced rotations when some of the locking features are activated.
            this.forcedYaw = this.yaw;
            this.forcedPitch = this.pitch;
        }
    }

    private void updateCustomRotations(double yawChange, double pitchChange, boolean updateYaw, boolean updatePitch, float pitchLimit)
    {
        if (updateYaw)
        {
            this.forcedYaw += yawChange * 0.15D;
        }

        if (updatePitch)
        {
            this.forcedPitch = net.minecraft.util.math.MathHelper.clamp(this.forcedPitch + pitchChange * 0.15D, -pitchLimit, pitchLimit);
        }
    }
}
