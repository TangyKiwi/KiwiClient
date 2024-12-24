package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.gui.MainMenu;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.tangykiwi.kiwiclient.KiwiClient.discord;
import static com.tangykiwi.kiwiclient.KiwiClient.discordRPC;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Inject(method = "init()V", at = @At("HEAD"))
    private void init(CallbackInfo info) {
        KiwiClient.mc.setScreen(new MainMenu());
        discordRPC.details = "Idle";
        discordRPC.state = "Main Menu";
        discord.Discord_UpdatePresence(discordRPC);
    }
}