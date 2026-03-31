package com.tangykiwi.kiwiclient.module.render;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.event.WorldRenderEvent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ModeSetting;
import com.tangykiwi.kiwiclient.module.setting.SliderSetting;
import com.tangykiwi.kiwiclient.util.WorldUtils;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;
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

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StorageESP extends Module {
    private Map<BlockEntity, Integer> blockEntities = new HashMap<>();

    private Set<BlockPos> blacklist = new HashSet<>();

    public StorageESP() {
        super("StorageESP","Highlights storage containers", GLFW.GLFW_KEY_R, Category.RENDER,
            new ModeSetting("Mode", "ESP mode", "Box+Fill", "Box", "Fill"),
            new SliderSetting("Box", "Box line thickness", 0.1, 4, 2, 1),
            new SliderSetting("Fill", "Fill opacity", 0, 1, 0.3, 2));
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
            int color = getColorForBlock(be);

            if (color != -1) {
                blockEntities.put(be, color);
            }
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onRender(WorldRenderEvent.Post event) {
        for (Map.Entry<BlockEntity, Integer> e: blockEntities.entrySet()) {
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

            float opacity = getSetting(2).asSlider().getValueFloat();
            if (getSetting(0).asMode().asMode().getValue() == 0 || getSetting(0).asMode().asMode().getValue() == 2) {
                RenderUtils.drawBoxFilled(box, (int)(opacity * 255) << 24 | e.getValue());
            }

            if (getSetting(0).asMode().asMode().getValue() == 0 || getSetting(0).asMode().asMode().getValue() == 1) {
                RenderUtils.drawBoxOutline(box, (int)(opacity * 255) << 24 | e.getValue(), getSetting(1).asSlider().getValue());
            }
        }

        blacklist.clear();
    }

    private int getColorForBlock(BlockEntity be) {
        if (be instanceof TrappedChestBlockEntity) {
            return ((int)(0.75F * 255) << 16 | 0 << 8 | 0);
        } else if (be instanceof ChestBlockEntity) {
            return (255 << 16 | (int)(0.6F * 255) << 8 | (int)(0.3F * 255));
        } else if (be instanceof BarrelBlockEntity) {
            return ((int)(0.5F * 255) << 16 | (int)(0.5F * 255) << 8 | (int)(0.5F * 255));
        } else if (be instanceof EnderChestBlockEntity) {
            return ((int)(0.5F * 255) << 16 | (int)(0.2F * 255) << 8 | 255);
//        } else if (be instanceof AbstractFurnaceBlockEntity) {
//            return new float[] { 0.5F, 0.5F, 0.5F };
//        } else if (be instanceof DispenserBlockEntity) {
//            return new float[] { 0.55F, 0.55F, 0.7F };
//        } else if (be instanceof HopperBlockEntity) {
//            return new float[] { 0.45F, 0.45F, 0.6F };
        } else if (be instanceof ShulkerBoxBlockEntity) {
            return (255 << 16 | (int)(0.05F * 255) << 8 | 255);
//        } else if (be instanceof BrewingStandBlockEntity) {
//            return new float[] { 0.5F, 0.4F, 0.2F };
        }

        return -1;
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
