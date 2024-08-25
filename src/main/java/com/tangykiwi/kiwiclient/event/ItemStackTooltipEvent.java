package com.tangykiwi.kiwiclient.event;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public record ItemStackTooltipEvent(ItemStack itemStack, List<Text> list) {}