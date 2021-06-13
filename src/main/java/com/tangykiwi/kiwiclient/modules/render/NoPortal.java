package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.OnMoveEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class NoPortal extends Module {
    public NoPortal() {
        super("NoPortal", "Removes portal animation interference", KEY_UNBOUND, Category.RENDER);
    }

    @Subscribe
    public void onClientMove(OnMoveEvent event) {
        if (doesBoxTouchBlock(mc.player.getBoundingBox(), Blocks.NETHER_PORTAL)) {
            mc.player.lastNauseaStrength = -1f;
            mc.player.nextNauseaStrength = -1f;
        }
    }

    public static boolean doesBoxTouchBlock(Box box, Block block) {
        for (int x = (int) Math.floor(box.minX); x < Math.ceil(box.maxX); x++) {
            for (int y = (int) Math.floor(box.minY); y < Math.ceil(box.maxY); y++) {
                for (int z = (int) Math.floor(box.minZ); z < Math.ceil(box.maxZ); z++) {
                    if (MinecraftClient.getInstance().world.getBlockState(new BlockPos(x, y, z)).getBlock() == block) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
