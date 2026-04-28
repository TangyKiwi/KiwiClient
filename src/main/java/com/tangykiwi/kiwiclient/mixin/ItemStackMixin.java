package com.tangykiwi.kiwiclient.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.ItemStackTooltipEvent;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @ModifyReturnValue(method = "getTooltipLines", at = @At("RETURN"))
    private List<Component> onGetTooltip(List<Component> original) {
        if (mc.level != null) {
            ItemStackTooltipEvent event = new ItemStackTooltipEvent((ItemStack) (Object) this, original);
            KiwiClient.eventBus.post(event);
            return event.list();
        }

        return original;
    }
}
