package com.tangykiwi.kiwiclient.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method="renderMainHud", at=@At(value="TAIL"), cancellable=true)
    private void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci){
        DrawOverlayEvent event = new DrawOverlayEvent(context);
        KiwiClient.eventBus.post(event);
        if (event.isCancelled()) ci.cancel();
    }
}
