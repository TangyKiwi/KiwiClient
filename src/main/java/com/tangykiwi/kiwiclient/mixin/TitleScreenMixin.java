package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.util.font.FontManager;
import com.tangykiwi.kiwiclient.util.font.FontRenderer;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static com.tangykiwi.kiwiclient.KiwiClient.discord;
import static com.tangykiwi.kiwiclient.KiwiClient.discordRPC;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Inject(method = "init()V", at = @At("HEAD"))
    private void init(CallbackInfo info) {
        discordRPC.details = "Idle";
        discordRPC.state = "Main Menu";
        discord.Discord_UpdatePresence(discordRPC);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        String title = KiwiClient.NAME + " v" + KiwiClient.VERSION + " - MC " + KiwiClient.MC_VERSION;
        FontRenderer fontRenderer = KiwiClient.fontManager.getSize(8, FontManager.Type.CONSOLAS);
        float height = fontRenderer.getStringHeight(title);
        float width = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(context.getMatrices(), title, 1, 2, new Color(0xFFFFFF));
        fontRenderer.drawStringWithShadow(context.getMatrices(), title, 1, 2 + height + 2, new Color(0xFFFFFF));
        fontRenderer.drawCenteredString(context.getMatrices(), title, 1 + width / 2, 2 + 2 * height + 4, new Color(0xFFFFFF));
        fontRenderer.drawCenteredStringWithShadow(context.getMatrices(), title, 1 + width / 2, 2 + 3 * height + 6, new Color(0xFFFFFF));
    }
}