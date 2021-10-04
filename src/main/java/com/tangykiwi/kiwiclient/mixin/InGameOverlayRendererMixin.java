package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.render.NoRender;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(InGameOverlayRenderer.class)
public abstract class InGameOverlayRendererMixin {
    @ModifyArg(
        method = "renderFireOverlay(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/util/math/MatrixStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/VertexConsumer;color(FFFF)Lnet/minecraft/client/render/VertexConsumer;"
        ),
        index = 3
    )
    private static float renderFireOverlay_opacity(float alpha) {
        NoRender noRender = (NoRender) KiwiClient.moduleManager.getModule(NoRender.class);
        if(noRender.isEnabled() && noRender.getSetting(0).asToggle().state) {
            return (float) noRender.getSetting(0).asToggle().getChild(0).asSlider().getValue();
        }
        return 1;
    }

    @ModifyArg(
        method = "renderFireOverlay(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/util/math/MatrixStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V"
        ),
        index = 1
    )
    private static double renderFireOverlay_translate(double y) {
        NoRender noRender = (NoRender) KiwiClient.moduleManager.getModule(NoRender.class);
        if(noRender.isEnabled() && noRender.getSetting(0).asToggle().state) {
            return -1.0D + noRender.getSetting(0).asToggle().getChild(1).asSlider().getValue();
        }
        return -0.3;
    }
}
