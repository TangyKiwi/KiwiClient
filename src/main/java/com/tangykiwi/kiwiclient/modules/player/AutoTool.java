package com.tangykiwi.kiwiclient.modules.player;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.mixin.ClientPlayerInteractionManagerAccessor;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class AutoTool extends Module {
    public AutoTool() {
        super("AutoTool", "Autoswaps to best possible tool in your hotbar", KEY_UNBOUND, Category.PLAYER);
    }

    @Subscribe
    public void onTick(TickEvent event) {
        ClientPlayerInteractionManager interactionManager = mc.interactionManager;
        if(interactionManager.isBreakingBlock()) {
            BlockPos blockPos = ((ClientPlayerInteractionManagerAccessor) interactionManager).getCurrentBreakingPos();
            BlockState blockState = mc.world.getBlockState(blockPos);
            swap(blockState);
        }
    }

    public void swap(BlockState state) {
        float best = 1f;
        int index = -1;
        int optAirIndex = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = Objects.requireNonNull(mc.player).getInventory().getStack(i);
            if (stack.getItem() == Items.AIR) {
                optAirIndex = i;
            }
            float s = stack.getMiningSpeedMultiplier(state);
            if (s > best) {
                index = i;
            }
        }
        if (index != -1) {
            mc.player.getInventory().selectedSlot = index;
        } else {
            if (optAirIndex != -1) {
                mc.player.getInventory().selectedSlot = optAirIndex;
            }
        }
    }
}
