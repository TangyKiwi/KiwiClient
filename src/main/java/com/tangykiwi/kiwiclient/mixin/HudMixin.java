package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.SpeedComponent;
import com.tangykiwi.kiwiclient.util.Textures;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

@Mixin(Hud.class)
public class HudMixin {
    public int frame = 0, tick = 0;

    @Inject(method = "extractRenderState", at = @At("TAIL"), cancellable = true)
    private void extractRenderState(GuiGraphicsExtractor context, final DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!mc.getDebugOverlay().showDebugScreen()) {
            context.blit(RenderPipelines.GUI_TEXTURED, Textures.DUCK_GIF, 0, 0, 0, frame * 76, 55, 76, 55, 1144);

            if (mc.player != null && SpeedComponent.getSpeed() > 0) {
                tick += 1;
                if (tick == 5) {
                    frame = (frame + 1) % 15;
                    tick = 0;
                }
            }
        }

        DrawOverlayEvent event = new DrawOverlayEvent(context);
        KiwiClient.eventBus.post(event);
        if (event.isCancelled()) ci.cancel();
    }
}
