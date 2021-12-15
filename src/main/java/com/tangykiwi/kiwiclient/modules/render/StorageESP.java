package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.event.WorldRenderEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ModeSetting;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;
import com.tangykiwi.kiwiclient.util.WorldUtils;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;
import com.tangykiwi.kiwiclient.util.render.color.QuadColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.*;
import net.minecraft.block.enums.ChestType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StorageESP extends Module {
    private Map<BlockEntity, float[]> blockEntities = new HashMap<>();

    private Set<BlockPos> blacklist = new HashSet<>();

    public StorageESP() {
        super("StorageESP","Highlights storage containers", GLFW.GLFW_KEY_R, Category.RENDER,
            new ModeSetting("Mode", "Box+Fill", "Box", "Fill").withDesc("ESP Mode"),
            new SliderSetting("Box", 0.1, 4, 2, 1).withDesc("Box line thickness"),
            new SliderSetting("Fill", 0, 1, 0.3, 2).withDesc("Fill opacity"));
    }

    @Override
    public void onDisable() {
        blockEntities.clear();

        super.onDisable();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTick(TickEvent event) {
        blockEntities.clear();

        for (BlockEntity be: WorldUtils.getBlockEntities()) {
            float[] color = getColorForBlock(be);

            if (color != null) {
                blockEntities.put(be, color);
            }
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onRender(WorldRenderEvent.Post event) {
        for (Map.Entry<BlockEntity, float[]> e: blockEntities.entrySet()) {
            if (blacklist.contains(e.getKey().getPos())) {
                continue;
            }

            Box box = new Box(e.getKey().getPos());

            Block block = e.getKey().getCachedState().getBlock();

            if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST || block == Blocks.ENDER_CHEST) {
                box = box.contract(0.06);
                box = box.offset(0, -0.06, 0);

                Direction dir = getChestDirection(e.getKey().getPos());
                if (dir != null) {
                    box = box.expand(Math.abs(dir.getOffsetX()) / 2d, 0, Math.abs(dir.getOffsetZ()) / 2d);
                    box = box.offset(dir.getOffsetX() / 2d, 0, dir.getOffsetZ() / 2d);
                    blacklist.add(e.getKey().getPos().offset(dir));
                }
            }

            if (getSetting(0).asMode().mode == 0 || getSetting(0).asMode().mode == 2) {
                RenderUtils.drawBoxFill(box, QuadColor.single(e.getValue()[0], e.getValue()[1], e.getValue()[2], getSetting(2).asSlider().getValueFloat()));
            }

            if (getSetting(0).asMode().mode == 0 || getSetting(0).asMode().mode == 1) {
                RenderUtils.drawBoxOutline(box, QuadColor.single(e.getValue()[0], e.getValue()[1], e.getValue()[2], 1f), getSetting(1).asSlider().getValueFloat());
            }
        }

        blacklist.clear();
    }

    private float[] getColorForBlock(BlockEntity be) {
        if (be instanceof TrappedChestBlockEntity) {
            return new float[] {0.75F, 0.0F, 0.0F};
        } else if (be instanceof ChestBlockEntity) {
            return new float[] { 1F, 0.6F, 0.3F };
        } else if (be instanceof BarrelBlockEntity) {
            return new float[] { 0.5F, 0.5F, 0.5F };
        } else if (be instanceof EnderChestBlockEntity) {
            return new float[]{0.5F, 0.2F, 1F};
//        } else if (be instanceof AbstractFurnaceBlockEntity) {
//            return new float[] { 0.5F, 0.5F, 0.5F };
//        } else if (be instanceof DispenserBlockEntity) {
//            return new float[] { 0.55F, 0.55F, 0.7F };
//        } else if (be instanceof HopperBlockEntity) {
//            return new float[] { 0.45F, 0.45F, 0.6F };
        } else if (be instanceof ShulkerBoxBlockEntity) {
            return new float[] { 1F, 0.05F, 1F };
//        } else if (be instanceof BrewingStandBlockEntity) {
//            return new float[] { 0.5F, 0.4F, 0.2F };
        }

        return null;
    }

    /** returns the direction of the other chest if its linked, otherwise null **/
    private Direction getChestDirection(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);

        if (state.getBlock() instanceof ChestBlock && state.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
            return ChestBlock.getFacing(state);
        }

        return null;
    }
}
