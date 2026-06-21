package com.tangykiwi.kiwiclient.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.tangykiwi.kiwiclient.util.tooltip.CustomBannerGuiElementRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderBuffers;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;<init>(Lnet/minecraft/client/renderer/state/gui/GuiRenderState;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;Ljava/util/List;)V"))
    private List<PictureInPictureRenderer<?>> addSpecialRenderers(List<PictureInPictureRenderer<?>> list) {
        List<PictureInPictureRenderer<?>> result = new ArrayList<>(list.size() + 1);
        result.addAll(list);
        result.add(new CustomBannerGuiElementRenderer(renderBuffers.bufferSource(), minecraft.getAtlasManager()));
        return result;
    }
}
