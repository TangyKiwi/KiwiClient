package com.tangykiwi.kiwiclient.mixin;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tangykiwi.kiwiclient.gui.MainMenu;
import com.tangykiwi.kiwiclient.util.Textures;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.util.Identifier;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "renderBackgroundTexture", at = @At("HEAD"), cancellable = true)
    private static void renderBackgroundTexture(DrawContext context, Identifier texture, int x, int y, float u, float v, int width, int height, CallbackInfo ci) {
        if(!(mc.currentScreen instanceof PackScreen) && !(mc.currentScreen instanceof MainMenu) && !(mc.currentScreen instanceof SocialInteractionsScreen) && !(mc.currentScreen instanceof GameMenuScreen)) {            
            ci.cancel();

            if(mc.currentScreen instanceof OptionsScreen) {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, Textures.MENU3, x, y, u, v, width, height, width, height);
            } else {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, Textures.MENU2, x, y, u, v, width, height, width, height);
            }
        }
    }
}
