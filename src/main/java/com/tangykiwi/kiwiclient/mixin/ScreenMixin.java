package com.tangykiwi.kiwiclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.gui.mainmenu.MainMenu;
import com.tangykiwi.kiwiclient.mixininterface.ITooltipData;
import com.tangykiwi.kiwiclient.modules.client.ClickGui;
import com.tangykiwi.kiwiclient.modules.other.Background;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
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
import java.util.Objects;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow @Nullable protected MinecraftClient client;

    @Shadow public int height;

    @Shadow public int width;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "method_32635", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void onComponentConstruct(List<TooltipComponent> list, TooltipData data, CallbackInfo info) {
        if (data instanceof ITooltipData) {
            list.add(((ITooltipData) data).getComponent());
            info.cancel();
        }
    }

    @Inject(method = "renderBackgroundTexture", at = @At("HEAD"), cancellable = true)
    public void renderBackgroundTexture(int vOffset, CallbackInfo ci) {
        if(!(this.client.currentScreen instanceof PackScreen) && KiwiClient.moduleManager.getModule(Background.class).isEnabled()) {
            ci.cancel();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            if(this.client.currentScreen instanceof SocialInteractionsScreen) return;
            else if(this.client.currentScreen instanceof OptionsScreen) {
                RenderSystem.setShaderTexture(0, KiwiClient.MENU3);
            }
            else {
                RenderSystem.setShaderTexture(0, KiwiClient.MENU2);
            }
            Screen.drawTexture(new MatrixStack(), 0, 0, 0, 0, this.width, this.height, this.width, this.height);
            if (!(this.client.currentScreen instanceof MainMenu)) DrawableHelper.fill(new MatrixStack(), 0, 0, this.width, this.height, new Color(0, 0, 0, 140).getRGB());
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
