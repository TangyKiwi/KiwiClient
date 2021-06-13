package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.render.Zoom;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin
{
    @Inject(at = {@At("RETURN")}, method = {"onMouseScroll(JDD)V"})
    private void onOnMouseScroll(long long_1, double double_1, double double_2,
                                 CallbackInfo ci)
    {
        ((Zoom) KiwiClient.moduleManager.getModule(Zoom.class)).onMouseScroll(double_2);
    }

    /**
    @Inject(at = {@At("INVOKE")}, method = {"onMouseButton"})
    private void onOnMouseButton(long window, int button, int action, int mods, CallbackInfo callbackInfo) {
        MouseButtonEvent event = new MouseButtonEvent(button);
        KiwiClient.eventBus.post(event);
        if(event.isCancelled()) callbackInfo.cancel();
    } */
}
