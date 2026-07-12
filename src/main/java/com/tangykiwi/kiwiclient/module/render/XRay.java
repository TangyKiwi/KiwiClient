package com.tangykiwi.kiwiclient.module.render;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.ChunkOcclusionEvent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.util.ArrayList;
import java.util.List;

public class XRay extends Module {
    public ArrayList<Block> blocks = new ArrayList<>(List.of(
        Blocks.COAL_ORE,
        Blocks.DEEPSLATE_COAL_ORE,
        Blocks.COAL_BLOCK,
        Blocks.COPPER_ORE,
        Blocks.DEEPSLATE_COPPER_ORE,
        Blocks.RAW_COPPER_BLOCK,
        Blocks.IRON_ORE,
        Blocks.DEEPSLATE_IRON_ORE,
        Blocks.IRON_BLOCK,
        Blocks.RAW_IRON_BLOCK,
        Blocks.GOLD_ORE,
        Blocks.DEEPSLATE_GOLD_ORE,
        Blocks.GOLD_BLOCK,
        Blocks.RAW_GOLD_BLOCK,
        Blocks.LAPIS_ORE,
        Blocks.DEEPSLATE_LAPIS_ORE,
        Blocks.LAPIS_BLOCK,
        Blocks.REDSTONE_ORE,
        Blocks.DEEPSLATE_REDSTONE_ORE,
        Blocks.REDSTONE_BLOCK,
        Blocks.DIAMOND_ORE,
        Blocks.DEEPSLATE_DIAMOND_ORE,
        Blocks.DIAMOND_BLOCK,
        Blocks.NETHER_GOLD_ORE,
        Blocks.ANCIENT_DEBRIS,
        Blocks.NETHERITE_BLOCK,
        Blocks.SPAWNER,
        Blocks.END_PORTAL_FRAME
    )); 

    public XRay() {
        super("XRay", "Shows ores", KEY_UNBOUND, Category.RENDER
        /*new ToggleSetting("Fluids", "Show fluids, toggle xray to see changes", true)*/);
        for(Block block : Blocks.COPPER_BLOCK.asList()) {
            blocks.add(block);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();

        mc.levelRenderer.invalidateCompiledGeometry(mc.level, mc.options, mc.gameRenderer.mainCamera(), mc.getBlockColors());
    }

    @Override
    public void onDisable() {
        mc.levelRenderer.invalidateCompiledGeometry(mc.level, mc.options, mc.gameRenderer.mainCamera(), mc.getBlockColors());

        super.onDisable();
    }

    // light handling in BlockBehaviorMixin

    @Subscribe
    public void onChunkOcclusion(ChunkOcclusionEvent e) {
        e.cancel();
    }

    public boolean modifyDrawSide(BlockState state, BlockGetter view, BlockPos pos, Direction facing, boolean returns) {
        return blocks.contains(state.getBlock());
    }

    private static final ThreadLocal<BlockPos.MutableBlockPos> EXPOSED_POS = ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);

    public boolean isExposed(BlockPos blockPos) {
        for (Direction direction : Direction.values()) {
            if (!mc.level.getBlockState(EXPOSED_POS.get().setWithOffset(blockPos, direction)).isSolidRender())
                return true;
        }

        return false;
    }
}
