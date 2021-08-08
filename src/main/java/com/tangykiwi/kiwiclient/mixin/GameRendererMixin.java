package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.render.Zoom;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.GameRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GameRenderer.class, priority = 1001)
public abstract class GameRendererMixin
{
    @Redirect(
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/client/option/GameOptions;fov:D",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 0),
            method = {"getFov(Lnet/minecraft/client/render/Camera;FZ)D"})
    private double getFov(GameOptions options)
    {
        return ((Zoom) KiwiClient.moduleManager.getModule(Zoom.class)).changeFovBasedOnZoom(options.fov);
    }
}
