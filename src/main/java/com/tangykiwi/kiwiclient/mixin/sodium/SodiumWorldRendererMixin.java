package com.tangykiwi.kiwiclient.mixin.sodium;

import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.util.FogParameters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.NoRender;
import com.tangykiwi.kiwiclient.module.render.XRay;

@Mixin(SodiumWorldRenderer.class)
public abstract class SodiumWorldRendererMixin {
    @Unique
    private static final FogParameters DISABLED_FOG = new FogParameters(0, 0, 0, 0, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);

    @ModifyVariable(method = "setupTerrain", at = @At("HEAD"), argsOnly = true, name = "fogParameters")
    private FogParameters modifyFogParameters(FogParameters fogParameters) {
        NoRender noRender = (NoRender) KiwiClient.moduleManager.getModule(NoRender.class);
        if (noRender.isEnabled() && noRender.getSetting("Fog").asToggle().getValue()) return DISABLED_FOG;

        return fogParameters;
    }

    @ModifyVariable(method = "setupTerrain", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private boolean modifyUseOcclusionCulling(boolean useOcclusionCulling) {
        return useOcclusionCulling && !KiwiClient.moduleManager.getModule(XRay.class).isEnabled();
    }
}
