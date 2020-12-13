package com.tangykiwi.kiwiclient.registry;

import com.tangykiwi.kiwiclient.KiwiClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {

    public static final Item FABRIC_ITEM = new Item(new Item.Settings().group(ItemGroup.MATERIALS));

    public static void registerItems() {
        Registry.register(Registry.ITEM, new Identifier(KiwiClient.MOD_ID, "fabric_item"), FABRIC_ITEM);
    }
}
