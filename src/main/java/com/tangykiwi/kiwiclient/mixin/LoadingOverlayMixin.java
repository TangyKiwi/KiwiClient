package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.util.Textures;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Optional;
import java.util.function.Consumer;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin {
    @Final @Shadow private boolean fadeIn;
    @Shadow private float currentProgress;
    @Shadow private long fadeOutStart = -1L;
    @Shadow private long fadeInStart = -1L;
    @Final @Shadow private ReloadInstance reload;
    @Final @Shadow private Consumer<Optional<Throwable>> onFinish;

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    public void render(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        ci.cancel();
        renderCustom(context, mouseX, mouseY, delta);
    }

    public void renderCustom(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        int i = mc.getWindow().getGuiScaledWidth();
        int j = mc.getWindow().getGuiScaledHeight();
        long l = Util.getMillis();
        if (fadeIn && fadeInStart == -1L) {
            fadeInStart = l;
        }

        float f = fadeOutStart > -1L ? (float) (l - fadeOutStart) / 1000.0F : -1.0F;
        float g = fadeInStart > -1L ? (float) (l - fadeInStart) / 500.0F : -1.0F;
        // float h = 0.0F;
        int k;
        if (f >= 1.0F) {
            if (mc.screen != null)
                mc.screen.extractRenderState(context, 0, 0, delta);

            k = Mth.ceil((1.0F - Mth.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
            context.fill(0, 0, i, j, withAlpha(new Color(0x070015).getRGB(), k));
            // h = 1.0F - Mth.clamp(f - 1.0F, 0.0F, 1.0F);
        } else if (fadeIn) {
            if (mc.screen != null && g < 1.0F)
                mc.screen.extractRenderState(context, mouseX, mouseY, delta);

            k = Mth.ceil(Mth.clamp((double) g, 0.15, 1.0) * 255.0);
            context.fill(0, 0, i, j, withAlpha(new Color(0x070015).getRGB(), k));
            // h = Mth.clamp(g, 0.0F, 1.0F);
        } else {
            k = new Color(0x070015).getRGB();
            // float m = (float) (k >> 16 & 255) / 255.0F;
            // float n = (float) (k >> 8 & 255) / 255.0F;
            // float o = (float) (k & 255) / 255.0F;
            // h = 1.0F;
        }

        k = (int) ((double) context.guiWidth() * 0.5);
        int p = (int) ((double) context.guiHeight() * 0.5);
        int barY = (int)((double) context.guiHeight() * 0.8325);

        float t = this.reload.getActualProgress();
        this.currentProgress = Mth.clamp(this.currentProgress * 0.95F + t * 0.050000012F, 0.0F, 1.0F);

        int blitAlpha = ARGB.color(Math.round((1.0F - Mth.clamp(f, 0.0F, 1.0F)) * 255.0F), 255, 255, 255);
        context.blit(RenderPipelines.GUI_TEXTURED, Textures.LOGO2, k - 175, p - 35, 0, 0, (int) (350 * currentProgress), 70, 350, 70, blitAlpha);
        this.extractProgressBar(context, i / 2 - 175, barY - 5, i / 2 + 175, barY + 5, 1.0F - Mth.clamp(f, 0.0F, 1.0F));


        if (f >= 2.0F) {
            mc.setOverlay(null);
        }

        if (fadeOutStart == -1L && reload.isDone() && (!fadeIn || g >= 2.0F)) {
            try {
                reload.checkExceptions();
                onFinish.accept(Optional.empty());
            } catch (Throwable var23) {
                onFinish.accept(Optional.of(var23));
            }

            fadeOutStart = Util.getMillis();
            if (mc.screen != null) {
                mc.screen.init(mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
            }
        }
    }

    private static int withAlpha(int color, int alpha) {
        return color & 16777215 | alpha << 24;
    }

    private void extractProgressBar(final GuiGraphicsExtractor graphics, final int x0, final int y0, final int x1, final int y1, final float fade) {
        int width = Mth.ceil((float)(x1 - x0 - 2) * this.currentProgress);
        int alpha = Math.round(fade * 255.0F);
        int white = ARGB.color(alpha, 255, 162, 42);
        graphics.fill(x0 + 2, y0 + 2, x0 + width, y1 - 2, white);
        graphics.fill(x0 + 1, y0, x1 - 1, y0 + 1, white);
        graphics.fill(x0 + 1, y1, x1 - 1, y1 - 1, white);
        graphics.fill(x0, y0, x0 + 1, y1, white);
        graphics.fill(x1, y0, x1 - 1, y1, white);
   }
}
