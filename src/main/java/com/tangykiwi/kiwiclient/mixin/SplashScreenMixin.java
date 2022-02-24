package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.gui.LoadingScreen;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashOverlay.class)
public class SplashScreenMixin {

    @Shadow
    @Final
    private static Identifier LOGO;

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;)V", at = @At("TAIL"), cancellable=true)
    private static void init(MinecraftClient client, CallbackInfo ci) {
        Utils.mc.getWindow().setIcon(SplashScreenMixin.class.getResourceAsStream("/assets/kiwiclient/icon64.png"), SplashScreenMixin.class.getResourceAsStream("/assets/kiwiclient/icon128.png"));
        if(KiwiClient.moduleManager.getModule(com.tangykiwi.kiwiclient.modules.other.LoadingScreen.class).isEnabled()) {
            client.getTextureManager().registerTexture(LOGO, new LoadingScreen(LOGO));
        }
    }
}
