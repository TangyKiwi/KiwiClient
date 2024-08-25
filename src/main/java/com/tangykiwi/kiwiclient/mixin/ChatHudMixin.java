package com.tangykiwi.kiwiclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.AddMessageEvent;
import com.tangykiwi.kiwiclient.mixininterface.IChatHUD;
import com.tangykiwi.kiwiclient.mixininterface.IChatHUDLine;
import com.tangykiwi.kiwiclient.mixininterface.IChatHUDLineVisible;
import com.tangykiwi.kiwiclient.mixininterface.IMessageHandler;
import com.tangykiwi.kiwiclient.modules.client.BetterChat;
import com.tangykiwi.kiwiclient.util.StringCharacterVisitor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin implements IChatHUD {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Shadow @Final private List<ChatHudLine.Visible> visibleMessages;
    @Shadow private int scrolledLines;

    @Unique private int nextId;
    @Unique private boolean skipOnAddMessage;

    @Shadow
    public abstract void addMessage(Text message, @Nullable MessageSignatureData signatureData, @Nullable MessageIndicator indicator);

    @Shadow
    public abstract void addMessage(Text message);

    @Shadow
    @Final
    private List<ChatHudLine> messages;

    @Override
    public void kiwiclient$add(Text message, int id) {
        nextId = id;
        addMessage(message);
        nextId = 0;
    }

    @Inject(method = "addVisibleMessage", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", shift = At.Shift.AFTER))
    private void onAddMessageAfterNewChatHudLineVisible(ChatHudLine message, CallbackInfo ci) {
        ((IChatHUDLine) (Object) visibleMessages.getFirst()).kiwiclient$setId(nextId);
    }

    @Inject(method = "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", shift = At.Shift.AFTER))
    private void onAddMessageAfterNewChatHudLine(ChatHudLine message, CallbackInfo ci) {
        ((IChatHUDLine) (Object) messages.getFirst()).kiwiclient$setId(nextId);
    }

    @SuppressWarnings("DataFlowIssue")
    @ModifyExpressionValue(method = "addVisibleMessage", at = @At(value = "NEW", target = "(ILnet/minecraft/text/OrderedText;Lnet/minecraft/client/gui/hud/MessageIndicator;Z)Lnet/minecraft/client/gui/hud/ChatHudLine$Visible;"))
    private ChatHudLine.Visible onAddMessage_modifyChatHudLineVisible(ChatHudLine.Visible line, @Local(ordinal = 1) int j) {
        IMessageHandler handler = (IMessageHandler) client.getMessageHandler();
        if (handler == null) return line;

        IChatHUDLineVisible clientLine = (IChatHUDLineVisible) (Object) line;

        clientLine.kiwiclient$setSender(handler.kiwiclient$getSender());
        clientLine.kiwiclient$setStartOfEntry(j == 0);

        return line;
    }

    @ModifyExpressionValue(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At(value = "NEW", target = "(ILnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)Lnet/minecraft/client/gui/hud/ChatHudLine;"))
    private ChatHudLine onAddMessage_modifyChatHudLine(ChatHudLine line) {
        IMessageHandler handler = (IMessageHandler) client.getMessageHandler();
        if (handler == null) return line;

        ((IChatHUDLine) (Object) line).kiwiclient$setSender(handler.kiwiclient$getSender());
        return line;
    }

    @Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", cancellable = true)
    private void onAddMessage(Text message, MessageSignatureData signatureData, MessageIndicator indicator, CallbackInfo ci) {
        if (skipOnAddMessage) return;

        AddMessageEvent event = new AddMessageEvent(message, nextId);
        KiwiClient.eventBus.post(event);
        if(event.isCancelled()) ci.cancel();
        else {
            visibleMessages.removeIf((msg) -> ((IChatHUDLine) (Object) msg).kiwiclient$getId() == nextId && nextId != 0);
            messages.removeIf((msg) -> ((IChatHUDLine) (Object) msg).kiwiclient$getId() == nextId && nextId != 0);

            if (event.modified) {
                ci.cancel();

                skipOnAddMessage = true;
                addMessage(event.message, signatureData, indicator);
                skipOnAddMessage = false;
            }
        }
    }

    @ModifyExpressionValue(method = "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V", at = @At(value = "CONSTANT", args = "intValue=100"))
    private int maxLength(int size) {
        BetterChat betterChat = (BetterChat) KiwiClient.moduleManager.getModule(BetterChat.class);
        return betterChat.getSetting(5).asToggle().state && betterChat.getSetting(5).asToggle().getChild(0).asSlider().getValue() >= 100 ? size + betterChat.getSetting(5).asToggle().getChild(0).asSlider().getValueInt() : size;
    }

//    @Inject(method = "render", at = @At("TAIL"))
//    private void onRender(DrawContext context, int currentTick, int mouseX, int mouseY, CallbackInfo ci) {
//        BetterChat betterChat = (BetterChat) KiwiClient.moduleManager.getModule(BetterChat.class);
//        if(betterChat.isEnabled() && betterChat.getSetting(3).asToggle().state) {
//            if (mc.options.getChatVisibility().getValue() == ChatVisibility.HIDDEN) return;
//            int maxLineCount = mc.inGameHud.getChatHud().getVisibleLineCount();
//
//            double d = mc.options.getChatOpacity().getValue() * 0.8999999761581421D + 0.10000000149011612D;
//            double g = 9.0D * (mc.options.getChatLineSpacing().getValue() + 1.0D);
//            double h = -8.0D * (mc.options.getChatLineSpacing().getValue() + 1.0D) + 4.0D * mc.options.getChatLineSpacing().getValue() + 8.0D;
//
//            MatrixStack matrices = context.getMatrices();
//            matrices.push();
//            matrices.translate(2, -0.1f, 10);
//            RenderSystem.enableBlend();
//            for(int m = 0; m + this.scrolledLines < this.visibleMessages.size() && m < maxLineCount; ++m) {
//                ChatHudLine.Visible chatHudLine = this.visibleMessages.get(m + this.scrolledLines);
//                if (chatHudLine != null) {
//                    int x = currentTick - chatHudLine.addedTime();
//                    if (x < 200 || isChatFocused()) {
//                        double o = isChatFocused() ? 1.0D : getMessageOpacityMultiplier(x);
//                        if (o * d > 0.01D) {
//                            double s = ((double)(-m) * g);
//                            StringCharacterVisitor visitor = new StringCharacterVisitor();
//                            chatHudLine.content().accept(visitor);
//                            drawIcon(context, visitor.result.toString(), (int)(s + h), (float)(o * d));
//                        }
//                    }
//                }
//            }
//            RenderSystem.disableBlend();
//            matrices.pop();
//        }
//    }

    @Shadow
    private static double getMessageOpacityMultiplier(int age) {
        throw new AssertionError();
    }

    @Shadow @Final private MinecraftClient client;

    private void drawIcon(DrawContext context, String line, int y, float opacity) {
        Identifier skin = getMessageTexture(line);
        if (skin != null) {
            RenderSystem.setShaderColor(1, 1, 1, opacity);
            context.drawTexture(skin, 0, y, 8, 8, 8.0F, 8.0F,8, 8, 64, 64);
            context.drawTexture(skin, 0, y, 8, 8, 40.0F, 8.0F,8, 8, 64, 64);
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
    }

    private static Identifier getMessageTexture(String message) {
        if (mc.getNetworkHandler() == null) return null;
        for (String part : message.split("(§.)|[^\\w]")) {
            if (part.isBlank()) continue;
            PlayerListEntry p = mc.getNetworkHandler().getPlayerListEntry(part);
            if (p != null) {
                return p.getSkinTextures().texture();
            }
        }
        return Identifier.of("kiwiclient:textures/hud/terminal.png");
    }
}
