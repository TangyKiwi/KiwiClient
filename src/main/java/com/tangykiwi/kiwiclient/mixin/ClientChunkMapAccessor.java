package com.tangykiwi.kiwiclient.mixin;

import java.util.concurrent.atomic.AtomicReferenceArray;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.world.level.chunk.LevelChunk;

@Mixin(ClientChunkCache.Storage.class)
public interface ClientChunkMapAccessor {
    @Accessor("chunks")
    AtomicReferenceArray<LevelChunk> getChunks();
}
