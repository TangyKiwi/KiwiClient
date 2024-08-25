package com.tangykiwi.kiwiclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.gui.mainmenu.MainMenu;
import com.tangykiwi.kiwiclient.modules.client.ClickGui;
import com.tangykiwi.kiwiclient.modules.other.Background;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow @Nullable protected MinecraftClient client;

    @Shadow public int height;

    @Shadow public int width;

    @Inject(method = "renderBackgroundTexture", at = @At("HEAD"), cancellable = true)
    private static void renderBackgroundTexture(DrawContext context, Identifier texture, int x, int y, float u, float v, int width, int height, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(!(client.currentScreen instanceof PackScreen) && KiwiClient.moduleManager.getModule(Background.class).isEnabled()) {
            ci.cancel();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            if(client.currentScreen instanceof SocialInteractionsScreen) return;
            else if(client.currentScreen instanceof OptionsScreen) {
                context.drawTexture(KiwiClient.MENU3, x, y, u, v, width, height, width, height);
            }
            else {
                context.drawTexture(KiwiClient.MENU2, x, y, u, v, width, height, width, height);
                RenderSystem.setShaderTexture(0, KiwiClient.MENU2);
            }
            if (!(client.currentScreen instanceof MainMenu)) context.fill(0, 0, width, height, new Color(0, 0, 0, 140).getRGB());
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if(keyCode == GLFW.GLFW_KEY_SEMICOLON
                && this.client.currentScreen instanceof MainMenu || this.client.currentScreen instanceof TitleScreen) {
            Utils.mc.setScreen(ClickGui.clickGui);
        }
    }
}
