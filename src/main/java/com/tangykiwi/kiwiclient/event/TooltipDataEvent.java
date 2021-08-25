package com.tangykiwi.kiwiclient.event;

import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;

public class TooltipDataEvent {
    public TooltipData tooltipData;
    public ItemStack itemStack;

    public TooltipDataEvent(ItemStack itemStack) {
        this.tooltipData = null;
        this.itemStack = itemStack;
    }
}
