package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.render.ActiveMods;
import com.tangykiwi.kiwiclient.util.ColorUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method="render", at=@At(value="TAIL"), cancellable=true)
    private void render(CallbackInfo info){
        if(!MinecraftClient.getInstance().options.debugEnabled) {
            MinecraftClient client = MinecraftClient.getInstance();
            TextRenderer textRenderer = client.textRenderer;
            TextureManager textureManager = client.getTextureManager();
            MatrixStack matrixStack = new MatrixStack();

            textureManager.bindTexture(new Identifier("kiwiclient:textures/duck.png"));
            client.inGameHud.drawTexture(matrixStack, 0, 0, 0, 0, 130, 130);
            //textRenderer.draw(matrixStack, KiwiClient.name + " v" + KiwiClient.version, 22, 6, -1);
        }

        DrawOverlayEvent event = new DrawOverlayEvent(new MatrixStack());
        KiwiClient.eventBus.post(event);
        if (event.isCancelled()) info.cancel();
    }
}
