package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.movement.SafeWalk;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }
    
    @Inject(method = "isStayingOnGroundSurface", at = @At("HEAD"), cancellable = true)
    protected void clipAtLedge(CallbackInfoReturnable<Boolean> cir) {
        if (!level().isClientSide()) return;

        SafeWalk safeWalk = (SafeWalk) KiwiClient.moduleManager.getModule(SafeWalk.class);  

        if (safeWalk.isEnabled()) {
            if (safeWalk.getSetting(0).asToggle().getValue()) {
                boolean closeToEdge = false;
                boolean isSprinting = mc.options.keySprint.isDown();

                AABB playerBox = mc.player.getBoundingBox();
                AABB adjustedBox = getAdjustedPlayerBox(playerBox);

                if (mc.level.noCollision(mc.player, adjustedBox) && mc.player.onGround()) closeToEdge = true;

                if (!isSprinting) {
                    if (closeToEdge) {
                        mc.player.input.keyPresses = new Input(
                            mc.player.input.keyPresses.forward(),
                            mc.player.input.keyPresses.backward(),
                            mc.player.input.keyPresses.left(),
                            mc.player.input.keyPresses.right(),
                            mc.player.input.keyPresses.jump(),
                            true,
                            mc.player.input.keyPresses.sprint()
                        );
                    } else {
                        cir.setReturnValue(true);
                    }
                }
            } else {
                cir.setReturnValue(true);
            }
        }
    }

    private AABB getAdjustedPlayerBox(AABB playerBox) {
        return playerBox
            .expandTowards(0, -mc.player.maxUpStep(), 0)
            .inflate(-0.3, 0, -0.3);
    }
}
