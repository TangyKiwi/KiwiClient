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
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private int heldItemTooltipFade;

    @Shadow
    private ItemStack currentStack;

    @Shadow
    private int scaledWidth;

    @Shadow
    private int scaledHeight;

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

    @Inject(at = @At("HEAD"), method = "renderHeldItemTooltip")
    public void onInjectTooltip(MatrixStack matrixStack, CallbackInfo info) {
        if(KiwiClient.moduleManager.getModule(Tooltips.class).isEnabled()) {
            if(KiwiClient.moduleManager.getModule(Tooltips.class).getSetting(0).asToggle().state) {
                this.client.getProfiler().push("selectedItemName");
                if (this.heldItemTooltipFade > 0 && !this.currentStack.isEmpty()) {
                    MutableText mutableText = (new LiteralText("")).append(this.currentStack.getName()).formatted(this.currentStack.getRarity().formatting);
                    if (this.currentStack.hasCustomName()) {
                        mutableText.formatted(Formatting.ITALIC);
                    }

                    int mainItemNameWidth = client.textRenderer.getWidth(mutableText);
                    int j = (this.scaledWidth - mainItemNameWidth) / 2;
                    int hotbarOffset = this.scaledHeight - 59;
                    if (!this.client.interactionManager.hasStatusBars()) {
                        hotbarOffset += 14;
                    }

                    int opacity = (int)((float)this.heldItemTooltipFade * 256.0F / 10.0F);
                    if (opacity > 255) {
                        opacity = 255;
                    }

                    if (opacity > 0) {
                        RenderSystem.enableBlend();
                        RenderSystem.defaultBlendFunc();
                        int var10001 = j - 2;
                        int var10002 = hotbarOffset - 2;
                        int var10003 = j + mainItemNameWidth + 2;
                        fill(matrixStack, var10001, var10002, var10003, hotbarOffset + 9 + 2, this.client.options.getTextBackgroundColor(0));
                        if (currentStack.getItem() == Items.SUSPICIOUS_STEW){
                            NbtCompound tag = currentStack.getTag();
                            if (tag != null) {
                                NbtList effects = tag.getList("Effects", 10);
                                int effectsCount = effects.size();
                                for (int i = 0; i < effectsCount; i++) {
                                    tag = effects.getCompound(i);
                                    int duration = tag.getInt("EffectDuration");
                                    StatusEffect effect = StatusEffect.byRawId(tag.getByte("EffectId"));
                                    String time = ChatUtil.ticksToString(duration);
                                    Text completeText = new TranslatableText(effect.getTranslationKey()).append(" "+time);
                                    j = (this.scaledWidth - client.textRenderer.getWidth(completeText)) / 2;
                                    client.textRenderer.drawWithShadow(matrixStack, completeText, (float)j, (float)hotbarOffset-(i*14)-14, 13421772 + (opacity << 24));
                                }
                            }
                        }
                        RenderSystem.disableBlend();
                    }
                }
            }
        }
    }
}
