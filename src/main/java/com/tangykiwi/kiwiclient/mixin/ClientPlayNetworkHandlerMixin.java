package com.tangykiwi.kiwiclient.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.GameJoinEvent;
import com.tangykiwi.kiwiclient.event.GameLeftEvent;
import com.tangykiwi.kiwiclient.event.SendChatMessageEvent;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler {
    @Shadow
    private ClientWorld world;

    @Shadow
    public abstract void sendChatMessage(String content);

    @Unique
    private boolean ignoreChatMessage;

    @Unique
    private boolean worldNotNull;

    protected ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
        super(client, connection, connectionState);
    }

    @Inject(at = @At("HEAD"), method = "onGameJoin")
    private void onGameJoinHead(GameJoinS2CPacket packet, CallbackInfo info) {
        worldNotNull = world != null;
    }

    @Inject(at = @At("TAIL"), method = "onGameJoin")
    private void onGameJoinTail(GameJoinS2CPacket packet, CallbackInfo info) {
        if(worldNotNull) {
            GameLeftEvent event = new GameLeftEvent();
            KiwiClient.eventBus.post(event);
        } else {
            GameJoinEvent event = new GameJoinEvent();
            KiwiClient.eventBus.post(event);
        }
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo ci) {
        if (ignoreChatMessage) return;

        if (!message.startsWith(KiwiClient.PREFIX) && !message.startsWith("/")) {
            SendChatMessageEvent event = new SendChatMessageEvent(message);
            KiwiClient.eventBus.post(event);

            if (!event.isCancelled()) {
                ignoreChatMessage = true;
                sendChatMessage(event.message);
                ignoreChatMessage = false;
            }
            ci.cancel();
            return;
        }

        if (message.startsWith(KiwiClient.PREFIX)) {
            try {
                KiwiClient.commandManager.dispatch(message.substring(KiwiClient.PREFIX.length()));
            } catch (CommandSyntaxException e) {
                Utils.mc.inGameHud.getChatHud().addMessage(Text.literal(e.getMessage()));
            }
            Utils.mc.inGameHud.getChatHud().addToMessageHistory(message);
            ci.cancel();
        }
    }
}
