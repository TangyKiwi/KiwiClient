package com.tangykiwi.kiwiclient.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.ItemStackTooltipEvent;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @ModifyReturnValue(method = "getTooltip", at = @At("RETURN"))
    private List<Text> onGetTooltip(List<Text> original) {
        if (mc.world != null) {
            ItemStackTooltipEvent event = new ItemStackTooltipEvent((ItemStack) (Object) this, original);
            KiwiClient.eventBus.post(event);
            return event.list();
        }

        return original;
    }
}
