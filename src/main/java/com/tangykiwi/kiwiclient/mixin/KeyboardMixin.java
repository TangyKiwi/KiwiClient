package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.Module;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At(value = "RETURN", ordinal = 4), require = 1, cancellable = true)
    public void onKey(long window, int key, int scancode, int i, int j, CallbackInfo info) {
        for(Module m : KiwiClient.moduleManager.moduleList) {
            if(m.getKeyCode() == key) m.toggle();
        }
    }

}
