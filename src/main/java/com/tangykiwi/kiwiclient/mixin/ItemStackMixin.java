package com.tangykiwi.kiwiclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.ItemStackTooltipEvent;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @ModifyReturnValue(method = "getTooltip", at = @At("RETURN"))
    private List<Text> onGetTooltip(List<Text> original) {
        if (Utils.canUpdate()) {
            ItemStackTooltipEvent event = new ItemStackTooltipEvent((ItemStack) ((Object) this), original);
            KiwiClient.eventBus.post(event);
            return event.list();
        }

        return original;
    }
}
