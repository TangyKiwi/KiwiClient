package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.client.BetterTab;
import com.tangykiwi.kiwiclient.util.render.color.ColorUtil;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 0), index = 1)
    private int modifyCount(int count) {
        BetterTab module = (BetterTab) KiwiClient.moduleManager.getModule(BetterTab.class);

        return module.isEnabled() ? (int) module.getSetting(0).asSlider().getValue() : count;
    }

    @Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
    public void getPlayerName(PlayerListEntry playerListEntry, CallbackInfoReturnable<Text> info) {
        BetterTab module = (BetterTab) KiwiClient.moduleManager.getModule(BetterTab.class);

        if (module.isEnabled()) info.setReturnValue(module.getPlayerName(playerListEntry));
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 1), index = 0)
    private int modifyWidth(int width) {
        BetterTab module = (BetterTab) KiwiClient.moduleManager.getModule(BetterTab.class);

        return module.isEnabled() && module.getSetting(1).asToggle().state ? width + 30 : width;
    }

    @Shadow
    protected void renderLatencyIcon(MatrixStack matrices, int width, int x, int y, PlayerListEntry entry) {}

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;renderLatencyIcon(Lnet/minecraft/client/util/math/MatrixStack;IIILnet/minecraft/client/network/PlayerListEntry;)V"))
    protected void renderLatencyIcon(PlayerListHud self, MatrixStack matrices, int width, int x, int y, PlayerListEntry entry) {
        BetterTab module = (BetterTab) KiwiClient.moduleManager.getModule(BetterTab.class);

        if (module.isEnabled() && module.getSetting(1).asToggle().state) {
            TextRenderer textRenderer = Utils.mc.textRenderer;

            int latency = clamp(entry.getLatency(), 0, 9999);
            int color = ColorUtil.getColorString(latency, 10, 20, 50, 75, 100, true);
            String text = latency + "ms";
            textRenderer.drawWithShadow(matrices, text, (float) x + width - textRenderer.getWidth(text), (float) y, color);
        } else {
            renderLatencyIcon(matrices, width, x, y, entry);
        }
    }

    public int clamp(int value, int min, int max) {
        if (value < min) return min;
        return Math.min(value, max);
    }
}
