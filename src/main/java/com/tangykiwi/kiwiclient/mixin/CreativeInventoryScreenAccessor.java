package com.tangykiwi.kiwiclient.mixin;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeInventoryScreen.class)
public interface CreativeInventoryScreenAccessor {
    @Accessor("selectedTab")
    static ItemGroup getSelectedTab() {
        return null;
    }
}
