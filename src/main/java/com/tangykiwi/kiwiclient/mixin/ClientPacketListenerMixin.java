package com.tangykiwi.kiwiclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.module.Module;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.CommandBuildContext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.util.Arrays;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo ci, @Local(argsOnly = true, name = "content") LocalRef<String> messageRef) {
        if (message.startsWith(KiwiClient.PREFIX)) {
            try {
                KiwiClient.commandManager.dispatch(message.substring(KiwiClient.PREFIX.length()));
            } catch (CommandSyntaxException e) {
                KiwiClient.LOGGER.error(e.getMessage());
            }

            mc.gui.hud.getChat().addRecentChat(message);
            ci.cancel();
        }
    }

    @Inject(method = "handleLogin", at = @At("TAIL"))
    private void onGameJoin(CallbackInfo ci) {
        for (Module m : KiwiClient.moduleManager.getEnabledMods(null)) {
            m.onEnable();
        }

        Arrays.fill(KiwiClient.tickRate.tickRates, 0);
        KiwiClient.tickRate.nextIndex = 0;
        KiwiClient.tickRate.timeGameJoined = KiwiClient.tickRate.timeLastTimeUpdate = System.currentTimeMillis();

        ClientPacketListener networkHandler = mc.getConnection();
        Command.REGISTRY_ACCESS = CommandBuildContext.simple(networkHandler.registryAccess(), networkHandler.enabledFeatures());

        KiwiClient.commandManager.DISPATCHER = new CommandDispatcher<>();
        for (Command command : KiwiClient.commandManager.COMMANDS) {
            command.registerTo(KiwiClient.commandManager.DISPATCHER);
        }
    }
}
