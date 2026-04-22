package com.tangykiwi.kiwiclient.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.NoRender;

import net.minecraft.client.renderer.fog.FogRenderer;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @ModifyExpressionValue(method = "getBuffer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/fog/FogRenderer;fogEnabled:Z", opcode = Opcodes.GETSTATIC))
    private boolean modifyFogEnabled(boolean original) {
        NoRender noRender = (NoRender) KiwiClient.moduleManager.getModule(NoRender.class);
        if (!noRender.isEnabled()) {
            return original;
        }
        return !noRender.getSetting("Fog").asToggle().getValue();
    }
}
