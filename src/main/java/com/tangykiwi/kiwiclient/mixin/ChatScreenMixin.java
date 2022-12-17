package com.tangykiwi.kiwiclient.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.SendChatMessageEvent;
import com.tangykiwi.kiwiclient.modules.client.BetterChat;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
    @Shadow protected TextFieldWidget chatField;

    @Shadow public abstract boolean sendMessage(String chatText, boolean addToHistory);

    @Unique
    private boolean ignoreChatMessage;

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setMaxLength(I)V", shift = At.Shift.AFTER))
    private void onInit(CallbackInfo info) {
        if (KiwiClient.moduleManager.getModule(BetterChat.class).getSetting(4).asToggle().state) chatField.setMaxLength(Integer.MAX_VALUE);
    }

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, boolean addToHistory, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (ignoreChatMessage) return;

        if (!message.startsWith(KiwiClient.PREFIX) && !message.startsWith("/")) {
            SendChatMessageEvent event = new SendChatMessageEvent(message);
            KiwiClient.eventBus.post(event);

            if (!event.isCancelled()) {
                ignoreChatMessage = true;
                sendMessage(event.message, addToHistory);
                ignoreChatMessage = false;
            }

            callbackInfoReturnable.setReturnValue(true);
            return;
        }

        if (message.startsWith(KiwiClient.PREFIX)) {
            try {
                KiwiClient.commandManager.dispatch(message.substring(KiwiClient.PREFIX.length()));
            } catch (CommandSyntaxException e) {
                Utils.mc.inGameHud.getChatHud().addMessage(Text.literal(e.getMessage()));
            }
            Utils.mc.inGameHud.getChatHud().addToMessageHistory(message);
            callbackInfoReturnable.setReturnValue(true);
        }
    }
}