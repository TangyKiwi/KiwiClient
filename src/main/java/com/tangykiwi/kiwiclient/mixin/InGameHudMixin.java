package com.tangykiwi.kiwiclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import com.tangykiwi.kiwiclient.modules.client.Tooltips;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.gui.DrawableHelper.fill;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method="render", at=@At(value="TAIL"), cancellable=true)
    private void render(CallbackInfo info){
        if(!MinecraftClient.getInstance().options.debugEnabled) {
            MinecraftClient client = MinecraftClient.getInstance();
            TextRenderer textRenderer = client.textRenderer;
            TextureManager textureManager = client.getTextureManager();
            MatrixStack matrixStack = new MatrixStack();

            RenderSystem.setShaderTexture(0, KiwiClient.DUCK);
            //textureManager.bindTexture(new Identifier("kiwiclient:textures/duck.png"));
            client.inGameHud.drawTexture(matrixStack, 0, 0, 0, 0, 130, 130);
            //textRenderer.draw(matrixStack, KiwiClient.name + " v" + KiwiClient.version, 22, 6, -1);
        }

        DrawOverlayEvent event = new DrawOverlayEvent(new MatrixStack());
        KiwiClient.eventBus.post(event);
        if (event.isCancelled()) info.cancel();
    }
}
