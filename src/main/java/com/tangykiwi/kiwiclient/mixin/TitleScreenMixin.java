package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.gui.mainmenu.MainMenu;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.tangykiwi.kiwiclient.KiwiClient.discordRPC;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text t) {
        super(t);
    }

    @Inject(method = "init()V", at = @At("HEAD"))
    private void init(CallbackInfo info) {
        if(KiwiClient.moduleManager.getModule(com.tangykiwi.kiwiclient.modules.other.MainMenu.class).isEnabled()) {
            this.client.setScreen(new MainMenu());
        }

        discordRPC.update("Idle", "Main Menu");
    }
}
