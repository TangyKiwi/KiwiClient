package com.tangykiwi.kiwiclient.event;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class TooltipDataEvent {
    public TooltipComponent tooltipData;
    public ItemStack itemStack;

    public TooltipDataEvent(ItemStack itemStack) {
        this.tooltipData = null;
        this.itemStack = itemStack;
    }
}
