package com.tangykiwi.kiwiclient.util.tooltip;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.BlockActivateEvent;
import com.tangykiwi.kiwiclient.event.OpenScreenEvent;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.util.collection.DefaultedList;

public class EChestMemory {
    public static DefaultedList<ItemStack> ITEMS = DefaultedList.ofSize(27, ItemStack.EMPTY);
    private static int echestOpenedState = -1;

    public EChestMemory() {

    }

    @Subscribe
    @AllowConcurrentEvents
    private static void onBlockActivate(BlockActivateEvent event) {
        if (event.blockState.getBlock() instanceof EnderChestBlock && echestOpenedState == 0) {
            echestOpenedState = 1;
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    private static void onOpenScreenEvent(OpenScreenEvent event) {
        if (echestOpenedState == 1 && event.getScreen() instanceof GenericContainerScreen) {
            echestOpenedState = 2;
            return;
        }
        if (echestOpenedState == 0) return;

        if (!(Utils.mc.currentScreen instanceof GenericContainerScreen)) return;
        GenericContainerScreenHandler container = ((GenericContainerScreen) Utils.mc.currentScreen).getScreenHandler();
        if (container == null) return;
        Inventory inv = container.getInventory();

        for (int i = 0; i < 27; i++) {
            ITEMS.set(i, inv.getStack(i));
        }

        echestOpenedState = 0;
    }
}