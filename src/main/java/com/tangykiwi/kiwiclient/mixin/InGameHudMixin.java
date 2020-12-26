package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.Module;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method="render", at=@At(value="TAIL"), cancellable=true)
    private void render(CallbackInfo info){
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        TextureManager textureManager = client.getTextureManager();
        MatrixStack matrixStack = new MatrixStack();

        int scaledWidth = client.getWindow().getScaledWidth();
        int scaledHeight = client.getWindow().getScaledHeight();

        textureManager.bindTexture(new Identifier("kiwiclient:textures/duck.png"));
        client.inGameHud.drawTexture(matrixStack, 0, 0, 0, 0, 130, 130);
        //textRenderer.draw(matrixStack, KiwiClient.name + " v" + KiwiClient.version, 22, 6, -1);

        int count = 0;
        ArrayList<Module> enabledMods = KiwiClient.moduleManager.getEnabledMods();
        for(Module m : enabledMods) {

            int offset = count * (textRenderer.fontHeight + 6);

            DrawableHelper.fill(matrixStack, scaledWidth - textRenderer.getWidth(m.getName()) - 10, offset, scaledWidth - textRenderer.getWidth(m.getName()) - 8, 6 + textRenderer.fontHeight + offset, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));
            DrawableHelper.fill(matrixStack, scaledWidth - textRenderer.getWidth(m.getName()) - 8, offset, scaledWidth, 6 + textRenderer.fontHeight + offset, 0x90000000);
            textRenderer.draw(matrixStack, m.getName(), scaledWidth - textRenderer.getWidth(m.getName()) - 2, 4 + offset, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));

            count++;
        }

    }
}
