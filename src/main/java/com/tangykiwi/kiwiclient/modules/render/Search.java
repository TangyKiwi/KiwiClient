package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.collect.Sets;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.ReceivePacketEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.event.WorldRenderEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ModeSetting;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;
import com.tangykiwi.kiwiclient.util.ChunkProcessor;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;
import com.tangykiwi.kiwiclient.util.render.color.LineColor;
import com.tangykiwi.kiwiclient.util.render.color.QuadColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.HashSet;
import java.util.Set;

public class Search extends Module {
    private Set<BlockPos> foundBlocks = Sets.newConcurrentHashSet();
    public Set<Block> blocks = new HashSet<>();

    private ChunkProcessor processor = new ChunkProcessor(1,
            (cp, chunk) -> {
                if (foundBlocks.size() > 1000000)
                    return;

                for (int x = 0; x < 16; x++) {
                    for (int y = mc.world.getBottomY(); y < mc.world.getTopY(); y++) {
                        for (int z = 0; z < 16; z++) {
                            BlockPos pos = new BlockPos(cp.getStartX() + x, y, cp.getStartZ() + z);
                            BlockState state = chunk.getBlockState(pos);
                            if (blocks.contains(state.getBlock())) {
                                foundBlocks.add(pos);
                            }
                        }
                    }
                }
            },
            (cp, chunk) ->
                    foundBlocks.removeIf(pos
                            -> pos.getX() >= cp.getStartX()
                            && pos.getX() <= cp.getEndX()
                            && pos.getZ() >= cp.getStartZ()
                            && pos.getZ() <= cp.getEndZ()),
            (pos, state) -> {
                if (blocks.contains(state.getBlock())) {
                    foundBlocks.add(pos);
                } else {
                    foundBlocks.remove(pos);
                }
            });

    private Set<Block> prevBlockList = new HashSet<>();
    private int oldViewDistance = -1;

    public Search() {
        super("Search", "Highlights certain blocks.", KEY_UNBOUND, Category.RENDER,
            new ModeSetting("Render", "Box+Fill", "Box", "Fill").withDesc("ESP Mode"),
            new SliderSetting("Box", 0.1, 4, 2, 1).withDesc("Box line thickness"),
            new SliderSetting("Fill", 0, 1, 0.3, 2).withDesc("Fill opacity"),
            new ToggleSetting("Tracers", false).withDesc("Draws lines to all found blocks").withChildren(
                new SliderSetting("Width", 0.1, 5, 1.5, 1).withDesc("Tracer line width"),
                new SliderSetting("Opacity", 0, 1, 0.75, 2).withDesc("Tracer line opacity")));
    }

    @Override
    public void onDisable() {
        foundBlocks.clear();
        prevBlockList.clear();
        processor.stop();

        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        processor.start();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTick(TickEvent event) {
        if (!prevBlockList.equals(blocks) || oldViewDistance != mc.options.getViewDistance().getValue()) {
            foundBlocks.clear();

            processor.submitAllLoadedChunks();

            prevBlockList = new HashSet<>(blocks);
            oldViewDistance = mc.options.getViewDistance().getValue();
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onReadPacket(ReceivePacketEvent event) {
        if (event.getPacket() instanceof DisconnectS2CPacket
                || event.getPacket() instanceof GameJoinS2CPacket
                || event.getPacket() instanceof PlayerRespawnS2CPacket) {
            foundBlocks.clear();
            prevBlockList.clear();
            processor.restartExecutor();
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onRender(WorldRenderEvent.Post event) {
        int mode = getSetting(0).asMode().mode;

        int i = 0;
        for (BlockPos pos : foundBlocks) {
            if (i > 3000)
                return;

            BlockState state = mc.world.getBlockState(pos);

            int[] color = getColorForBlock(state, pos);

            VoxelShape voxelShape = state.getOutlineShape(mc.world, pos);
            if (voxelShape.isEmpty()) {
                voxelShape = VoxelShapes.cuboid(0, 0, 0, 1, 1, 1);
            }

            if (mode == 0 || mode == 2) {
                int fillAlpha = (int) (getSetting(2).asSlider().getValue() * 255);

                for (Box box: voxelShape.getBoundingBoxes()) {
                    RenderUtils.drawBoxFill(box.offset(pos), QuadColor.single(color[0], color[1], color[2], fillAlpha));
                }
            }

            if (mode == 0 || mode == 1) {
                float outlineWidth = getSetting(1).asSlider().getValueFloat();

                for (Box box: voxelShape.getBoundingBoxes()) {
                    RenderUtils.drawBoxOutline(box.offset(pos), QuadColor.single(color[0], color[1], color[2], 255), outlineWidth);
                }
            }

            ToggleSetting tracers = getSetting(3).asToggle();
            if (tracers.state) {
                Vec3d lookVec = new Vec3d(0, 0, 75)
                        .rotateX(-(float) Math.toRadians(mc.gameRenderer.getCamera().getPitch()))
                        .rotateY(-(float) Math.toRadians(mc.gameRenderer.getCamera().getYaw()))
                        .add(mc.cameraEntity.getEyePos());

                RenderUtils.drawLine(
                        lookVec.x, lookVec.y, lookVec.z,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        LineColor.single(color[0], color[1], color[2], (int) (tracers.getChild(1).asSlider().getValue() * 255)),
                        tracers.getChild(0).asSlider().getValueFloat());
            }

            i++;
        }
    }

    public int[] getColorForBlock(BlockState state, BlockPos pos) {
        if (state.getBlock() == Blocks.NETHER_PORTAL) {
            return new int[] { 107, 0, 209 };
        }

        int color = state.getMapColor(mc.world, pos).color;
        return new int[] { (color & 0xff0000) >> 16, (color & 0xff00) >> 8, color & 0xff };
    }
}
