package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
    @ModifyReturnValue(method = "showTooltipWithItemInHand", at = @At("RETURN"))
    private boolean showTooltipWithItemInHand(boolean original, ItemStack item) {
        if (item.getTooltipImage().orElse(null) instanceof ClientTooltipComponent component) {
            return original || component.showTooltipWithItemInHand();
        }

        return original;
    }
}
