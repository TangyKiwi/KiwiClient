package com.tangykiwi.kiwiclient.mixin;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import com.tangykiwi.kiwiclient.gui.hudeditor.components.SpeedComponent;
import com.tangykiwi.kiwiclient.util.Textures;

// @Mixin(InGameHud.class)
// public class InGameHudMixin {
    
//     public int frame = 0, tick = 0;

//     @Inject(method="renderMainHud", at=@At(value="TAIL"), cancellable=true)
//     private void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci){
//         if (!mc.getDebugHud().shouldShowDebugHud()) {
//             // context.drawTexture(RenderPipelines.GUI_TEXTURED, Textures.DUCK, 0, 0, 0, 0, 53, 59, 53, 59);
//             context.drawTexture(RenderPipelines.GUI_TEXTURED, Textures.DUCK_GIF, 0, 0, 0, frame * 76, 55, 76, 55, 1144);

//             if (SpeedComponent.getSpeed() > 0) {
//                 tick += 1;
//                 if (tick == 5) {
//                     frame = (frame + 1) % 15;
//                     tick = 0;
//                 }
//             }
//         }

//         DrawOverlayEvent event = new DrawOverlayEvent(context);
//         KiwiClient.eventBus.post(event);
//         if (event.isCancelled()) ci.cancel();
//     }
// }
