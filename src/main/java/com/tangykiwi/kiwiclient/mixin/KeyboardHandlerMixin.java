package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.KeyPressEvent;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKeyEvent(long window, int action, KeyEvent input, CallbackInfo callbackInfo) {
        if (input != null) {
            KeyPressEvent event = new KeyPressEvent(input, action);
            KiwiClient.eventBus.post(event);
            if (event.isCancelled()) callbackInfo.cancel();
        }
    }
}
