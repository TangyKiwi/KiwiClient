package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.render.TNTimer;
import com.tangykiwi.kiwiclient.util.font.IFont;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TntEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.TntEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.text.DecimalFormat;

@Mixin(TntEntityRenderer.class)
public abstract class TntEntityRendererMixin extends EntityRenderer<TntEntity> {

    public DecimalFormat decimalFormat = new DecimalFormat("0.00");

    protected TntEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    public void render(TntEntity tntEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if(KiwiClient.moduleManager.getModule(TNTimer.class).isEnabled()) {
            renderLabel(tntEntity, matrixStack, vertexConsumerProvider, i);
        }
    }

    protected void renderLabel(TntEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        double d = this.dispatcher.getSquaredDistanceToCamera(entity);
        if (!(d > 4096.0D)) {
            String text = getTime(entity.getFuse());
            int color = getColor(entity.getFuse());

            float f = entity.getHeight() + 0.5F;

            matrices.push();
            matrices.translate(0.0D, f, 0.0D);
            matrices.multiply(this.dispatcher.getRotation());
            matrices.scale(-0.025F, -0.025F, 0.025F);
            final Matrix4f matrix4f = matrices.peek().getModel();

            float backgroundOpacity = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
            int backgroundColor = (int) (backgroundOpacity * 255.0F) << 24;

            float x = (float) (-IFont.CONSOLAS.getStringWidth(text) / 2);
            float y = -4;

            text = text + "s";

            IFont.CONSOLAS.drawString(matrices, text, x, y, color);
            DrawableHelper.fill(matrices, (int) (x - 1), (int) (y - 2), (int) (x + IFont.CONSOLAS.getStringWidth(text) + 2), (int) (y + IFont.CONSOLAS.getFontHeight() + 1), backgroundColor);
            matrices.pop();
        }
    }

    public String getTime(double ticks) {
        double timing = ticks / 20;

        return decimalFormat.format(timing);
    }

    public int getColor(double ticks) {
        double timing = ticks / 20;

        if (timing > 7d) {
            return 0x00AAAA;
        } else if (timing > 6d) {
            return 0x55FFFF;
        } else if (timing > 4d) {
            return 0x00AA00;
        } else if (timing > 3d) {
            return 0x55FF55;
        } else if (timing > 2d) {
            return 0xFFAA00;
        } else if (timing > 1d) {
            return 0xFF5555;
        } else if (timing > 0d) {
            return 0xAA0000;
        }

        return Color.WHITE.getRGB();
    }
}
