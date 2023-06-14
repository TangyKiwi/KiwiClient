package com.tangykiwi.kiwiclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.gui.mainmenu.MainMenu;
import com.tangykiwi.kiwiclient.mixininterface.ITooltipData;
import com.tangykiwi.kiwiclient.modules.client.ClickGui;
import com.tangykiwi.kiwiclient.modules.other.Background;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.awt.*;
import java.util.List;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow @Nullable protected MinecraftClient client;

    @Shadow public int height;

    @Shadow public int width;

    @Inject(method = "renderBackgroundTexture", at = @At("HEAD"), cancellable = true)
    public void renderBackgroundTexture(DrawContext context, CallbackInfo ci) {
        if(!(this.client.currentScreen instanceof PackScreen) && KiwiClient.moduleManager.getModule(Background.class).isEnabled()) {
            ci.cancel();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            if(this.client.currentScreen instanceof SocialInteractionsScreen) return;
            else if(this.client.currentScreen instanceof OptionsScreen) {
                context.drawTexture(KiwiClient.MENU3, 0, 0, 0, 0, this.width, this.height, this.width, this.height);
            }
            else {
                context.drawTexture(KiwiClient.MENU2, 0, 0, 0, 0, this.width, this.height, this.width, this.height);
                RenderSystem.setShaderTexture(0, KiwiClient.MENU2);
            }
            if (!(this.client.currentScreen instanceof MainMenu)) context.fill(0, 0, this.width, this.height, new Color(0, 0, 0, 140).getRGB());
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
