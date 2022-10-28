package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.OpenScreenEvent;
import com.tangykiwi.kiwiclient.modules.other.NoIP;
import com.tangykiwi.kiwiclient.modules.render.Freecam;
import com.tangykiwi.kiwiclient.util.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.tangykiwi.kiwiclient.KiwiClient.discordRPC;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
//    @Inject(method = "<init>", at = @At("TAIL"))
//    private void onInit(CallbackInfo info) {
//        KiwiClient.INSTANCE.onInitialize();
//    }

    @Inject(method="getWindowTitle", at=@At(value="TAIL"), cancellable=true)
    private void getWindowTitle(final CallbackInfoReturnable<String> info) {
        MinecraftClient client = (MinecraftClient) (Object) this;

        String title = KiwiClient.name + " v" + KiwiClient.version;

        ClientPlayNetworkHandler clientPlayNetworkHandler = client.getNetworkHandler();
        if (clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen()) {
            title += " - ";
            if (client.getServer() != null && !client.getServer().isRemote()) {
                title += I18n.translate("title.singleplayer");
                discordRPC.update("Playing", "Singleplayer");
            } else if (client.isConnectedToRealms()) {
                title += I18n.translate("title.multiplayer.realms");
                discordRPC.update("Playing", "Realms");
            } else if (client.getServer() == null && (client.getCurrentServerEntry() == null || !client.getCurrentServerEntry().isLocal())) {
                title += I18n.translate("title.multiplayer.other");
                if(KiwiClient.moduleManager.getModule(NoIP.class).isEnabled()) {
                    discordRPC.update("Playing", "Multiplayer");
                } else {
                    discordRPC.update("Playing", client.getCurrentServerEntry().address);
                }
            } else {
                title += I18n.translate("title.multiplayer.lan");
                discordRPC.update("Playing", "LAN Server");
            }
        }
        else {
            discordRPC.update("Idle", "Main Menu");
        }

        info.setReturnValue(title);
    }

    @Inject(at = @At("HEAD"), method = "setScreen", cancellable = true)
    public void openScreen(Screen screen, CallbackInfo info) {
        OpenScreenEvent event = new OpenScreenEvent(screen);
        KiwiClient.eventBus.post(event);
        if (event.isCancelled()) info.cancel();
    }

    @Inject(method = "stop", at = @At("HEAD"))
    public void shutdown(CallbackInfo info) {
        discordRPC.shutdown();
        KiwiClient.moduleManager.getModule(Freecam.class).setToggled(false);
        ConfigManager.saveModules("default");
        ConfigManager.saveClickGui("default");
    }
}
