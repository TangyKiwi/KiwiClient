package com.tangykiwi.kiwiclient.mixin;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.ESP;
import com.tangykiwi.kiwiclient.module.render.Freecam;
import com.tangykiwi.kiwiclient.util.EntityUtils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract EntityType<?> getType();

    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    private void updateChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        if ((Object) this != mc.player) return;

        Freecam freecam = (Freecam) KiwiClient.moduleManager.getModule(Freecam.class);

        if (freecam.isEnabled()) {
            freecam.changeLookDirection(cursorDeltaX * 0.15, cursorDeltaY * 0.15);
            ci.cancel();
        }
    }

    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    private void setESPColor(CallbackInfoReturnable<Integer> ci) {
        ESP esp = (ESP) KiwiClient.moduleManager.getModule(ESP.class);
        if (esp.isEnabled() && esp.getSetting("Mode").asMode().getValue() == 0) {
            if (this.getType() == EntityType.PLAYER) {
                ci.setReturnValue((255 << 24) | (255 << 16) | (255 << 8) | 255);
            } else if (EntityUtils.isMob(this.getType())) {
                ci.setReturnValue((255 << 24) | (255 << 16) | (0 << 8) | 0);
            } else if (EntityUtils.isAnimal(this.getType())) {
                ci.setReturnValue((255 << 24) | (77 << 16) | (255 << 8) | 77);
            } else {
                ci.setReturnValue((255 << 24) | (128 << 16) | (128 << 8) | 128);
            }
            ci.cancel();
        }
    }
}
