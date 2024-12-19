package com.tangykiwi.kiwiclient.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.command.CommandManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo ci) {
        if (message.startsWith(KiwiClient.PREFIX)) {
            try {
                KiwiClient.commandManager.dispatch(message.substring(KiwiClient.PREFIX.length()));
            } catch (CommandSyntaxException e) {
                KiwiClient.LOGGER.error(e.getMessage());
            }

            mc.inGameHud.getChatHud().addToMessageHistory(message);
            ci.cancel();
        }
    }
}
