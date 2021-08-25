package com.tangykiwi.kiwiclient.mixininterface;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;

public interface ITooltipData extends TooltipData {
    MinecraftClient mc = MinecraftClient.getInstance();
    TooltipComponent getComponent();
}
