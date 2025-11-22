package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.KeyPressEvent;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKeyEvent(long window, int action, KeyInput input, CallbackInfo callbackInfo) {
        if (input != null) {
            KeyPressEvent event = new KeyPressEvent(input, action);
            KiwiClient.eventBus.post(event);
            if (event.isCancelled()) callbackInfo.cancel();
        }
    }
}
