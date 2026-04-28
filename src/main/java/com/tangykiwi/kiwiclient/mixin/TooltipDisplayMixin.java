package com.tangykiwi.kiwiclient.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.client.Tooltips;

import net.minecraft.world.item.component.TooltipDisplay;

@Mixin(TooltipDisplay.class)
public class TooltipDisplayMixin {
    @ModifyExpressionValue(method = "shows", at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/component/TooltipDisplay;hideTooltip:Z", opcode = Opcodes.GETFIELD))
    private boolean modifyHideTooltip(boolean original) {
        return original /*&& !KiwiClient.moduleManager.getModule(Tooltips.class).isEnabled()*/;
    }

    @ModifyExpressionValue(method = "shows", at = @At(value = "INVOKE", target = "Ljava/util/SequencedSet;contains(Ljava/lang/Object;)Z"))
    private boolean modifyHiddenComponents(boolean original) {
        return original /*&& !KiwiClient.moduleManager.getModule(Tooltips.class).isEnabled()*/;
    }
}
