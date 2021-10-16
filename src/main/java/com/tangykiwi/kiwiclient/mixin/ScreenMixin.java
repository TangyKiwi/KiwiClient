package com.tangykiwi.kiwiclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.gui.mainmenu.MainMenu;
import com.tangykiwi.kiwiclient.mixininterface.ITooltipData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.awt.*;
import java.util.List;

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
        ci.cancel();
        if(this.client.currentScreen instanceof SocialInteractionsScreen) return;
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, KiwiClient.MENU);
        Screen.drawTexture(new MatrixStack(), 0, 0, 0, 0, this.width, this.height, this.width, this.height);
        if (!(this.client.currentScreen instanceof MainMenu)) DrawableHelper.fill(new MatrixStack(), 0, 0, this.width, this.height, new Color(0, 0, 0, 140).getRGB());
    }
}
