package com.tangykiwi.kiwiclient.module.other;

import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class DummyModule extends Module {
    public DummyModule() {
        super("DummyModule", "Dummy Module", -1, Category.OTHER);
    }

    ItemStack item = Items.DIAMOND_HELMET.getDefaultStack();

    public void test() {

    }
}
