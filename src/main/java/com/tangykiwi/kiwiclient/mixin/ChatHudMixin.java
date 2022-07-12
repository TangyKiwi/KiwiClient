package com.tangykiwi.kiwiclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.AddMessageEvent;
import com.tangykiwi.kiwiclient.mixininterface.IChatHUD;
import com.tangykiwi.kiwiclient.modules.client.BetterChat;
import com.tangykiwi.kiwiclient.util.StringCharacterVisitor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin implements IChatHUD {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Shadow
    @Final
    private List<ChatHudLine<OrderedText>> visibleMessages;

    @Shadow
    private int scrolledLines;

    @Shadow protected abstract void addMessage(Text message, int messageId, int timestamp, boolean refresh);

    @Inject(at = @At("HEAD"), method = "addMessage(Lnet/minecraft/text/Text;I)V", cancellable = true)
    private void onAddMessage(Text text, int id, CallbackInfo info) {
        AddMessageEvent event = new AddMessageEvent(text, id);
        KiwiClient.eventBus.post(event);
        if(event.isCancelled()) info.cancel();
    }

    @Redirect(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
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

            double d = mc.options.getChtOpacity().getValue() * 0.8999999761581421D + 0.10000000149011612D;
            double g = 9.0D * (mc.options.getChatLineSpacing().getValue() + 1.0D);
            double h = -8.0D * (mc.options.getChatLineSpacing().getValue() + 1.0D) + 4.0D * mc.options.getChatLineSpacing().getValue() + 8.0D;

            matrices.push();
            matrices.translate(2, -0.1f, 10);
            RenderSystem.enableBlend();
            for(int m = 0; m + this.scrolledLines < this.visibleMessages.size() && m < maxLineCount; ++m) {
                ChatHudLine<OrderedText> chatHudLine = this.visibleMessages.get(m + this.scrolledLines);
                if (chatHudLine != null) {
                    int x = tickDelta - chatHudLine.getCreationTick();
                    if (x < 200 || isChatFocused()) {
                        double o = isChatFocused() ? 1.0D : getMessageOpacityMultiplier(x);
                        if (o * d > 0.01D) {
                            double s = ((double)(-m) * g);
                            StringCharacterVisitor visitor = new StringCharacterVisitor();
                            chatHudLine.getText().accept(visitor);
                            drawIcon(matrices, visitor.result.toString(), (int)(s + h), (float)(o * d));
                        }
                    }
                }
            }
            RenderSystem.disableBlend();
            matrices.pop();
        }
    }

    @Override
    public void add(Text message, int messageId, int timestamp, boolean refresh) {
        addMessage(message, messageId, timestamp, refresh);
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
