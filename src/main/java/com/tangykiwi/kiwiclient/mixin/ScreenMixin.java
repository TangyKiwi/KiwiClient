package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.mixininterface.ITooltipData;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "method_32635", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void onComponentConstruct(List<TooltipComponent> list, TooltipData data, CallbackInfo info) {
        if (data instanceof ITooltipData) {
            list.add(((ITooltipData) data).getComponent());
            info.cancel();
        }
    }
}
