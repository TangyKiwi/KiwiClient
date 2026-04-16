package com.tangykiwi.kiwiclient.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class WorldUtils {

    public static List<ChunkAccess> getLoadedChunks() {
        List<ChunkAccess> chunks = new ArrayList<>();

        int viewDist = mc.options.renderDistance().get();

        for (int x = -viewDist; x <= viewDist; x++) {
            for (int z = -viewDist; z <= viewDist; z++) {
                ChunkAccess chunk = mc.level.getChunk((int) mc.player.getX() / 16 + x, (int) mc.player.getZ() / 16 + z);

                if (chunk != null) {
                    chunks.add(chunk);
                }
            }
        }

        return chunks;
    }

    public static List<BlockEntity> getBlockEntities() {
        List<BlockEntity> list = new ArrayList<>();

        for (ChunkAccess chunk : getLoadedChunks()) {
            for (BlockPos pos : chunk.getBlockEntitiesPos()) {
                BlockEntity blockEntity = chunk.getBlockEntity(pos);
                if (blockEntity != null) {
                    list.add(blockEntity);
                }
            }
        }
        
        return list;
    }

    public static Dimension getDimension() {
        if (mc.level == null) return Dimension.Overworld;

        return switch (mc.level.dimension().identifier().getPath()) {
            case "the_nether" -> Dimension.Nether;
            case "the_end" -> Dimension.End;
            default -> Dimension.Overworld;
        };
    }
}