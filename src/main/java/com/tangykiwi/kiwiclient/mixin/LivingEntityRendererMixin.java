package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.gui.mainmenu.MainMenu;
import com.tangykiwi.kiwiclient.gui.mainmenu.dummy.DummyClientPlayerEntity;
import com.tangykiwi.kiwiclient.modules.render.ESP;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity>
{
    @Redirect(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;isInvisibleTo(Lnet/minecraft/entity/player/PlayerEntity;)Z",
            ordinal = 0),
            method = {
                    "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"})
    private boolean canSeeEntity(LivingEntity e, PlayerEntity player)
    {
        if(KiwiClient.moduleManager.getModule(ESP.class).isEnabled()) {
            return false;
        }

        return e.isInvisibleTo(player);
    }

    @Inject(method = "hasLabel", at = @At("INVOKE"), cancellable = true)
    private void a(T livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (livingEntity instanceof DummyClientPlayerEntity || MinecraftClient.getInstance().currentScreen instanceof MainMenu) cir.setReturnValue(false);
    }
}
