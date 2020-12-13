package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
            } else if (client.isConnectedToRealms()) {
                stringBuilder.append(I18n.translate("title.multiplayer.realms"));
            } else if (client.getServer() == null && (client.getCurrentServerEntry() == null || !client.getCurrentServerEntry().isLocal())) {
                stringBuilder.append(I18n.translate("title.multiplayer.other"));
            } else {
                stringBuilder.append(I18n.translate("title.multiplayer.lan"));
            }
        }

        info.setReturnValue(stringBuilder.toString());
    }
}
