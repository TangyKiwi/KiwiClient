package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.render.Freecam;
import com.tangykiwi.kiwiclient.modules.render.Zoom;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

    @Redirect(
            method = {"updateMouse"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"
            )
    )
    private void updateMouseChangeLookDirection(ClientPlayerEntity player, double cursorDeltaX, double cursorDeltaY) {
        Freecam freecam = (Freecam) KiwiClient.moduleManager.getModule(Freecam.class);
        if (freecam.isEnabled()) {
            freecam.changeLookDirection(cursorDeltaX * 0.15D, cursorDeltaY * 0.15D);
        } else {
            player.changeLookDirection(cursorDeltaX, cursorDeltaY);
        }

    }

    /**
    @Inject(at = {@At("INVOKE")}, method = {"onMouseButton"})
    private void onOnMouseButton(long window, int button, int action, int mods, CallbackInfo callbackInfo) {
        MouseButtonEvent event = new MouseButtonEvent(button);
        KiwiClient.eventBus.post(event);
        if(event.isCancelled()) callbackInfo.cancel();
    } */
}
