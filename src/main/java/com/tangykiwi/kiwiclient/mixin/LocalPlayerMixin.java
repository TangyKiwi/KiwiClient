package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.SendMovementPacketEvent;

import net.minecraft.client.player.LocalPlayer;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Inject(method = "sendPosition", at = @At("HEAD"))
    private void onSendMovementPacketsHead(CallbackInfo info) {
        KiwiClient.eventBus.post(SendMovementPacketEvent.Pre.get());
    }

    @Inject(method = "sendPosition", at = @At("TAIL"))
    private void onSendMovementPacketsTail(CallbackInfo info) {
        KiwiClient.eventBus.post(SendMovementPacketEvent.Post.get());
    }
}
