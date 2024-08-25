package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.gui.mainmenu.MainMenu;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.tangykiwi.kiwiclient.KiwiClient.discordRPC;
import static com.tangykiwi.kiwiclient.KiwiClient.rpc;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Inject(method = "init()V", at = @At("HEAD"))
    private void init(CallbackInfo info) {
        if(KiwiClient.moduleManager.getModule(com.tangykiwi.kiwiclient.modules.other.MainMenu.class).isEnabled()) {
            MinecraftClient.getInstance().setScreen(new MainMenu());
        }

        discordRPC.details = "Idle";
        discordRPC.state = "Main Menu";
        rpc.Discord_UpdatePresence(discordRPC);
    }
}
