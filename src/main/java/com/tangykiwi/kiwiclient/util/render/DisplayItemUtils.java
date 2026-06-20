package com.tangykiwi.kiwiclient.util.render;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class DisplayItemUtils {
    public static ItemStack toStack(Item item) {
        if (item == Items.AIR) return ItemStack.EMPTY;
        return new ItemStack(directHolder(item));
    }

    public static ItemStack toStack(Block block) {
        return toStack(block.asItem());
    }

    public static ItemStack toStack(Item item, int count) {
        if (item == Items.AIR) return ItemStack.EMPTY;
        return new ItemStack(directHolder(item), count);
    }

    @SuppressWarnings("deprecation")
    private static Holder<Item> directHolder(Item item) {
        DataComponentMap components = DataComponentMap.builder()
            .addAll(DataComponents.COMMON_ITEM_COMPONENTS)
            .set(DataComponents.ITEM_MODEL, item.builtInRegistryHolder().key().identifier())
            .set(DataComponents.ITEM_NAME, Component.translatable(item.getDescriptionId()))
            .build();
        return Holder.direct(item, components);
    }
}
