package com.tangykiwi.kiwiclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.Freecam;

import net.minecraft.client.Camera;

import javax.swing.text.html.BlockView;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow private boolean detached;

    @ModifyVariable(method = "clipToSpace", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifyClipToSpace(float d) {
        if (KiwiClient.moduleManager.getModule(Freecam.class).isEnabled()) return 0;

        return d;
    }

    @Inject(method = "alignWithEntity", at = @At("TAIL"))
    private void onUpdateTail(float partialTicks, CallbackInfo info) {
        if (KiwiClient.moduleManager.getModule(Freecam.class).isEnabled()) {
            this.detached = true;
        }
    }

    @ModifyArgs(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setPosition(DDD)V"))
    private void onAlignSetPosArgs(Args args, @Local(argsOnly = true, name = "partialTicks") float partialTicks) {
        Freecam freecam = (Freecam) KiwiClient.moduleManager.getModule(Freecam.class);

        if (freecam.isEnabled()) {
            args.set(0, freecam.getX(partialTicks));
            args.set(1, freecam.getY(partialTicks));
            args.set(2, freecam.getZ(partialTicks));
        }
    }

    @ModifyArgs(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V"))
    private void onAlignSetRotationArgs(Args args, @Local(argsOnly = true, name = "partialTicks") float partialTicks) {
        Freecam freecam = (Freecam) KiwiClient.moduleManager.getModule(Freecam.class);

        if (freecam.isEnabled()) {
            args.set(0, (float) freecam.getYaw(partialTicks));
            args.set(1, (float) freecam.getPitch(partialTicks));
        }
    }
}
