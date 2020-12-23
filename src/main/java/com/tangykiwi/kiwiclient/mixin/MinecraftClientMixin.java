package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.OpenScreenEvent;
import com.tangykiwi.kiwiclient.util.DiscordRP;
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
    @Inject(method="getWindowTitle", at=@At(value="TAIL"), cancellable=true)
    private void getWindowTitle(final CallbackInfoReturnable<String> info){
        MinecraftClient client = (MinecraftClient) (Object) this;

        StringBuilder stringBuilder = new StringBuilder(KiwiClient.name + " v" + KiwiClient.version);

        ClientPlayNetworkHandler clientPlayNetworkHandler = client.getNetworkHandler();
        if (clientPlayNetworkHandler != null && clientPlayNetworkHandler.getConnection().isOpen()) {
            stringBuilder.append(" - ");
            if (client.getServer() != null && !client.getServer().isRemote()) {
                stringBuilder.append(I18n.translate("title.singleplayer"));
                discordRPC.update("Playing", "Singleplayer");
            } else if (client.isConnectedToRealms()) {
                stringBuilder.append(I18n.translate("title.multiplayer.realms"));
                discordRPC.update("Playing", "Realms");
            } else if (client.getServer() == null && (client.getCurrentServerEntry() == null || !client.getCurrentServerEntry().isLocal())) {
                stringBuilder.append(I18n.translate("title.multiplayer.other"));
                discordRPC.update("Playing", client.getCurrentServerEntry().address);
            } else {
                stringBuilder.append(I18n.translate("title.multiplayer.lan"));
                discordRPC.update("Playing", "LAN Server");
            }
        }

        info.setReturnValue(stringBuilder.toString());
    }

    @Inject(at = @At("HEAD"), method = "openScreen(Lnet/minecraft/client/gui/screen/Screen;)V", cancellable = true)
    public void openScreen(Screen screen, CallbackInfo info) {
        OpenScreenEvent event = new OpenScreenEvent(screen);
        KiwiClient.eventBus.post(event);
        if (event.isCancelled()) info.cancel();
    }

    @Inject(method = "stop", at = @At("HEAD"))
    public void shutdown(CallbackInfo info) {
        discordRPC.shutdown();
    }
}
