package com.tangykiwi.kiwiclient.mixin;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.Freecam;

import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    private void updateChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        if ((Object) this != mc.player) return;

        Freecam freecam = (Freecam) KiwiClient.moduleManager.getModule(Freecam.class);

        if (freecam.isEnabled()) {
            freecam.changeLookDirection(cursorDeltaX * 0.15, cursorDeltaY * 0.15);
            ci.cancel();
        }
    }
}
