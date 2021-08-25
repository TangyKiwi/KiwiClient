package com.tangykiwi.kiwiclient.event;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class ItemStackTooltipEvent {
    public ItemStack itemStack;
    public List<Text> list;

    public ItemStackTooltipEvent(ItemStack itemStack, List<Text> list) {
        this.itemStack = itemStack;
        this.list = list;
    }
}