package com.tangykiwi.kiwiclient.mixininterface;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public interface ITooltipData extends TooltipComponent {
    ClientTooltipComponent getComponent();
}
