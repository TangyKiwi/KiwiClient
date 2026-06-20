package com.tangykiwi.kiwiclient.mixin;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tangykiwi.kiwiclient.gui.MainMenu;
import com.tangykiwi.kiwiclient.util.Textures;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "extractMenuBackgroundTexture", at = @At("HEAD"), cancellable = true)
    private static void renderBackgroundTexture(GuiGraphicsExtractor context, Identifier texture, int x, int y, float u, float v, int width, int height, CallbackInfo ci) {
        if(!(mc.screen instanceof PackSelectionScreen) && !(mc.screen instanceof MainMenu) 
            && !(mc.screen instanceof SocialInteractionsScreen) && !(mc.screen instanceof PauseScreen)
            // lazy Iris shader pack check
            && !(mc.screen.getTitle().getString().equals("Shader Packs"))) {            
            ci.cancel();

            if(mc.screen instanceof OptionsScreen) {
                context.blit(RenderPipelines.GUI_TEXTURED, Textures.MENU3, x, y, u, v, width, height, width, height);
            } else {
                context.blit(RenderPipelines.GUI_TEXTURED, Textures.MENU2, x, y, u, v, width, height, width, height);
            }
        }
    }
}
