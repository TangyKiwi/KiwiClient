package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.render.XRay;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderLayers.class)
public class RenderLayersMixin {

//    @Inject(method = "getBlockLayer", at = @At("HEAD"), cancellable = true)
//    private static void getBlockLayer(BlockState state, CallbackInfoReturnable<RenderLayer> cir) {
//        XRay xray = (XRay) KiwiClient.moduleManager.getModule(XRay.class);
//
//        if (xray.isEnabled() && xray.getSetting(1).asToggle().state && !xray.isVisible(state.getBlock())) {
//            cir.setReturnValue(RenderLayer.getTranslucent());
//        }
//    }
}