package com.tangykiwi.kiwiclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import com.tangykiwi.kiwiclient.modules.client.MountHUD;
import com.tangykiwi.kiwiclient.modules.client.Tooltips;
import com.tangykiwi.kiwiclient.util.ColorUtil;
import com.tangykiwi.kiwiclient.util.font.IFont;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
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
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

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
            client.inGameHud.drawTexture(matrixStack, 0, 0, 0, 0, 130, 130);
            //textRenderer.draw(matrixStack, KiwiClient.name + " v" + KiwiClient.version, 22, 6, -1);

//            IFont.customFont.drawString(new MatrixStack(), "Test Render", 120, 4, ColorUtil.getRainbow(3, 0.8f, 1));
//            IFont.customFontBold.drawString(new MatrixStack(), "Test Render", 120, 26, ColorUtil.getRainbow(3, 0.8f, 1));
        }

        DrawOverlayEvent event = new DrawOverlayEvent(new MatrixStack());
        KiwiClient.eventBus.post(event);
        if (event.isCancelled()) info.cancel();
    }

    @Shadow @Final private MinecraftClient client;

    @ModifyConstant(method = "renderMountHealth", constant = @Constant(intValue = 39))
    private int renderMountHealth(int yOffset) {
        if (KiwiClient.moduleManager.getModule(MountHUD.class).isEnabled() && client.interactionManager.hasStatusBars()) {
            yOffset += 10;
        }
        return yOffset;
    }

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I"))
    private int renderFood(InGameHud inGameHud, LivingEntity entity) {
        if (KiwiClient.moduleManager.getModule(MountHUD.class).isEnabled()) return 0;
        if (entity != null && entity.isLiving()) {
            float f = entity.getMaxHealth();
            int i = (int)(f + 0.5F) / 2;
            if (i > 30) {
                i = 30;
            }

            return i;
        } else {
            return 0;
        }
    }

    @ModifyVariable(method = "renderStatusBars", at = @At(value = "STORE", ordinal = 1), ordinal = 10)
    private int moveAirUp(int y) {
        if (KiwiClient.moduleManager.getModule(MountHUD.class).isEnabled() && client.player.hasJumpingMount()) {
            y -= 10;
        }
        return y;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasJumpingMount()Z"))
    private boolean switchBar(ClientPlayerEntity player) {
        if (!client.interactionManager.hasExperienceBar() || !KiwiClient.moduleManager.getModule(MountHUD.class).isEnabled()) return player.hasJumpingMount();
        return player.hasJumpingMount() && client.options.keyJump.isPressed() || player.getMountJumpStrength() > 0;
    }
}
