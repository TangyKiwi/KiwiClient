package com.tangykiwi.kiwiclient.mixin;

import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import com.tangykiwi.kiwiclient.modules.client.MountHUD;
import com.tangykiwi.kiwiclient.modules.client.NoScoreboard;
import com.tangykiwi.kiwiclient.modules.client.PotionTimers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method="render", at=@At(value="TAIL"), cancellable=true)
    private void render(CallbackInfo info){
        if(!MinecraftClient.getInstance().options.debugEnabled) {
            MinecraftClient client = MinecraftClient.getInstance();
            MatrixStack matrixStack = new MatrixStack();

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, KiwiClient.DUCK);
            client.inGameHud.drawTexture(matrixStack, 0, 0, 0, 0, 53, 59, 53, 59);
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        DrawOverlayEvent event = new DrawOverlayEvent(new MatrixStack());
        KiwiClient.eventBus.post(event);
        if (event.isCancelled()) info.cancel();
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void renderScoreboardSidebar(MatrixStack matrices, ScoreboardObjective objective, CallbackInfo ci) {
        if(KiwiClient.moduleManager.getModule(NoScoreboard.class).isEnabled()) {
            ci.cancel();
            return;
        }
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
        return player.hasJumpingMount() && client.options.jumpKey.isPressed() || player.getMountJumpStrength() > 0;
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At("TAIL"))
    private void renderDurationOverlay(MatrixStack matrices, CallbackInfo c) {
        if(!KiwiClient.moduleManager.getModule(PotionTimers.class).isEnabled()) return;
        Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
        if (!collection.isEmpty()) {
            int beneficialCount = 0;
            int nonBeneficialCount = 0;
            for (StatusEffectInstance statusEffectInstance : Ordering.natural().reverse().sortedCopy(collection)) {
                StatusEffect statusEffect = statusEffectInstance.getEffectType();
                if (statusEffectInstance.shouldShowIcon()) {
                    int x = this.client.getWindow().getScaledWidth();
                    int y = 1;

                    if (statusEffect.isBeneficial()) {
                        beneficialCount++;
                        x -= 25 * beneficialCount;
                    } else {
                        nonBeneficialCount++;
                        x -= 25 * nonBeneficialCount;
                        y += 26;
                    }

                    String duration = getDurationAsString(statusEffectInstance);
                    int durationLength = client.textRenderer.getWidth(duration);
                    DrawableHelper.drawStringWithShadow(matrices, client.textRenderer, duration, x + 13 - (durationLength / 2), y + 14, 0x99FFFFFF);

                    int amplifier = statusEffectInstance.getAmplifier();
                    if (amplifier > 0) {
                        String amplifierString = (amplifier < 6) ? I18n.translate("potion.potency." + amplifier) : "**";
                        int amplifierLength = client.textRenderer.getWidth(amplifierString);
                        DrawableHelper.drawStringWithShadow(matrices, client.textRenderer, amplifierString, x + 22 - amplifierLength, y + 3, 0x99FFFFFF);
                    }
                }
            }
        }
    }

    @NotNull
    private String getDurationAsString(StatusEffectInstance statusEffectInstance) {
        int ticks = MathHelper.floor((float) statusEffectInstance.getDuration());
        int seconds = ticks / 20;

        if (ticks > 32147) {
            return "**";
        } else if (seconds > 60) {
            return seconds / 60 + ":" + (seconds % 60 < 10 ? "0" : "") + seconds % 60;
        } else {
            return String.valueOf(seconds);
        }
    }
}
