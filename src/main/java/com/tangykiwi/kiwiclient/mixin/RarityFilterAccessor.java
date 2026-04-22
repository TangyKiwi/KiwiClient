package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.levelgen.placement.RarityFilter;

@Mixin(RarityFilter.class)
public interface RarityFilterAccessor {
    @Accessor
    int getChance();
}
