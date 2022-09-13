package com.tangykiwi.kiwiclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.AddMessageEvent;
import com.tangykiwi.kiwiclient.mixininterface.IChatHUD;
import com.tangykiwi.kiwiclient.mixininterface.IChatHUDLine;
import com.tangykiwi.kiwiclient.modules.client.BetterChat;
import com.tangykiwi.kiwiclient.util.StringCharacterVisitor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin implements IChatHUD {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Shadow @Final private List<ChatHudLine.Visible> visibleMessages;
    @Shadow private int scrolledLines;

    @Unique private int nextId;
    @Unique private boolean skipOnAddMessage;

    @Shadow
    protected abstract void addMessage(Text message, @Nullable MessageSignatureData signature, int ticks, @Nullable MessageIndicator indicator, boolean refresh);

    @Shadow
    public abstract void addMessage(Text message);

    @Shadow
    @Final
    private List<ChatHudLine> messages;

    @Override
    public void add(Text message, int id) {
        nextId = id;
        addMessage(message);
        nextId = 0;
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 0, shift = At.Shift.AFTER))
    private void onAddMessageAfterNewChatHudLineVisible(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo info) {
        ((IChatHUDLine) (Object) visibleMessages.get(0)).setId(nextId);
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 1, shift = At.Shift.AFTER))
    private void onAddMessageAfterNewChatHudLine(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo info) {
        ((IChatHUDLine) (Object) messages.get(0)).setId(nextId);
    }

    @Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", cancellable = true)
    private void onAddMessage(Text message, @Nullable MessageSignatureData signature, int ticks, @Nullable MessageIndicator indicator, boolean refresh, CallbackInfo info) {
        if (skipOnAddMessage) return;

        AddMessageEvent event = new AddMessageEvent(message, nextId);
        KiwiClient.eventBus.post(event);
        if(event.isCancelled()) info.cancel();
        else {
            visibleMessages.removeIf((msg) -> ((IChatHUDLine) (Object) msg).getId() == nextId && nextId != 0);
            messages.removeIf((msg) -> ((IChatHUDLine) (Object) msg).getId() == nextId && nextId != 0);

            if (event.modified) {
                info.cancel();

                skipOnAddMessage = true;
                addMessage(event.message, signature, ticks, indicator, refresh);
                skipOnAddMessage = false;
            }
        }
    }

    @Redirect(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/ChatHud;visibleMessages:Ljava/util/List;")), at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
    private int addMessageListSizeProxy(List<ChatHudLine> list) {
        BetterChat betterChat = (BetterChat) KiwiClient.moduleManager.getModule(BetterChat.class);
        return betterChat.getSetting(5).asToggle().state && betterChat.getSetting(5).asToggle().getChild(0).asSlider().getValue() > 100 ? 1 : list.size();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrices, int tickDelta, CallbackInfo ci) {
        BetterChat betterChat = (BetterChat) KiwiClient.moduleManager.getModule(BetterChat.class);
        if(betterChat.isEnabled() && betterChat.getSetting(3).asToggle().state) {
            if (mc.options.getChatVisibility().getValue() == ChatVisibility.HIDDEN) return;
            int maxLineCount = mc.inGameHud.getChatHud().getVisibleLineCount();

            double d = mc.options.getChatOpacity().getValue() * 0.8999999761581421D + 0.10000000149011612D;
            double g = 9.0D * (mc.options.getChatLineSpacing().getValue() + 1.0D);
            double h = -8.0D * (mc.options.getChatLineSpacing().getValue() + 1.0D) + 4.0D * mc.options.getChatLineSpacing().getValue() + 8.0D;

            matrices.push();
            matrices.translate(2, -0.1f, 10);
            RenderSystem.enableBlend();
            for(int m = 0; m + this.scrolledLines < this.visibleMessages.size() && m < maxLineCount; ++m) {
                ChatHudLine.Visible chatHudLine = this.visibleMessages.get(m + this.scrolledLines);
                if (chatHudLine != null) {
                    int x = tickDelta - chatHudLine.addedTime();
                    if (x < 200 || isChatFocused()) {
                        double o = isChatFocused() ? 1.0D : getMessageOpacityMultiplier(x);
                        if (o * d > 0.01D) {
                            double s = ((double)(-m) * g);
                            StringCharacterVisitor visitor = new StringCharacterVisitor();
                            chatHudLine.content().accept(visitor);
                            drawIcon(matrices, visitor.result.toString(), (int)(s + h), (float)(o * d));
                        }
                    }
                }
            }
            RenderSystem.disableBlend();
            matrices.pop();
        }
    }

    private boolean isChatFocused() {
        return mc.currentScreen instanceof ChatScreen;
    }

    @Shadow
    private static double getMessageOpacityMultiplier(int age) {
        throw new AssertionError();
    }

    private void drawIcon(MatrixStack matrices, String line, int y, float opacity) {
        Identifier skin = getMessageTexture(line);
        if (skin != null) {
            RenderSystem.setShaderColor(1, 1, 1, opacity);
            RenderSystem.setShaderTexture(0, skin);
            DrawableHelper.drawTexture(matrices, 0, y, 8, 8, 8.0F, 8.0F,8, 8, 64, 64);
            DrawableHelper.drawTexture(matrices, 0, y, 8, 8, 40.0F, 8.0F,8, 8, 64, 64);
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
    }

    private static Identifier getMessageTexture(String message) {
        if (mc.getNetworkHandler() == null) return null;
        for (String part : message.split("(§.)|[^\\w]")) {
            if (part.isBlank()) continue;
            PlayerListEntry p = mc.getNetworkHandler().getPlayerListEntry(part);
            if (p != null) {
                return p.getSkinTexture();
            }
        }
        return new Identifier("kiwiclient:textures/hud/terminal.png");
    }
}
