package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;

@Mixin(CreativeModeInventoryScreen.class)
public interface CreativeModeInventoryScreenAccessor {
    @Accessor("selectedTab")
    static CreativeModeTab getSelectedTab() {
        return null;
    }
}
