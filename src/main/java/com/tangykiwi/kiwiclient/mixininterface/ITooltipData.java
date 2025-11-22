package com.tangykiwi.kiwiclient.mixininterface;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;

public interface ITooltipData extends TooltipData {
    TooltipComponent getComponent();
}
