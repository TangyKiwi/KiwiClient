package com.tangykiwi.kiwiclient.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.TooltipDataEvent;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
    private void onTooltipData(ItemStack stack, CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
        TooltipDataEvent event = new TooltipDataEvent(stack);
        KiwiClient.eventBus.post(event);
        if (event.tooltipData != null) {
            cir.setReturnValue(Optional.of(event.tooltipData));
        }
    }
}
