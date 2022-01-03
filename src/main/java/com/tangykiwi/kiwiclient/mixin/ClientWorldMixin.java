package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.client.Time;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Shadow
    @Final
    private ClientWorld.Properties clientWorldProperties;
    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At("TAIL"), method = "setTimeOfDay", cancellable = true)
    @Environment(EnvType.CLIENT)
    public void setTimeOfDay(long time, CallbackInfo ci) {
        if (KiwiClient.moduleManager.getModule(Time.class).isEnabled()) {
            this.clientWorldProperties.setTimeOfDay(KiwiClient.moduleManager.getModule(Time.class).getSetting(0).asSlider().getValueLong() * 1000);
        }
        else {ci.cancel();}
    }

    @Inject(method = "tickEntities", at = @At("HEAD"), cancellable = true)
    public void tickEntities(CallbackInfo info) {
        TickEvent event = new TickEvent();
        KiwiClient.eventBus.post(event);
        if (event.isCancelled())
            info.cancel();
    }
}
