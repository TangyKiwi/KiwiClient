package com.tangykiwi.kiwiclient.module.render;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.ChunkOcclusionEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.mixininterface.ISimpleOption;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.SliderSetting;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;

import net.minecraft.client.OptionInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.util.ArrayList;
import java.util.Arrays;

public class XRay extends Module {

    private double gamma;
    public ArrayList<Block> blocks = new ArrayList<Block>(Arrays.asList(
        Blocks.COAL_ORE,
        Blocks.DEEPSLATE_COAL_ORE,
        Blocks.COAL_BLOCK,
        Blocks.COPPER_ORE,
        Blocks.DEEPSLATE_COPPER_ORE,
        Blocks.COPPER_BLOCK,
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
    }

    @Override
    public void onEnable() {
        super.onEnable();

        mc.levelRenderer.allChanged();
        gamma = mc.options.gamma().get();
    }

    @Override
    public void onDisable() {
        // OptionInstance<Double> gammaOption = mc.options.gamma();
        // @SuppressWarnings("unchecked")
        // ISimpleOption<Double> gammaOption2 = (ISimpleOption<Double>)(Object)gammaOption;
        // gammaOption2.forceSetValue(gamma);
        mc.levelRenderer.allChanged();

        super.onDisable();
    }

    // @Subscribe
    // @AllowConcurrentEvents
    // public void onTick(TickEvent e) {
    //     OptionInstance<Double> gammaOption = mc.options.gamma();
    //     @SuppressWarnings("unchecked")
    //     ISimpleOption<Double> gammaOption2 = (ISimpleOption<Double>)(Object)gammaOption;
    //     gammaOption2.forceSetValue(16.0);
    // }

    // light handling in BlockBehaviorMixin

    @Subscribe
    public void onChunkOcclusion(ChunkOcclusionEvent e) {
        e.cancel();
    }

    public boolean modifyDrawSide(BlockState state, BlockGetter view, BlockPos pos, Direction facing, boolean returns) {
        return blocks.contains(state.getBlock());
        // if (!returns && blocks.contains(state.getBlock())) {
        //     BlockPos adjPos = pos.relative(facing);
        //     BlockState adjState = view.getBlockState(adjPos);
        //     return adjState.getFaceOcclusionShape(facing.getOpposite()) != Shapes.block() || adjState.getBlock() != state.getBlock() || !adjState.isSolidRender() || !blocks.contains(adjState.getBlock());
        // }

        // return returns;
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
