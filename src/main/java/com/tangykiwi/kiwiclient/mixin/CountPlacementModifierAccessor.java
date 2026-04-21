package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.placement.CountPlacement;

@Mixin(CountPlacement.class)
public interface CountPlacementModifierAccessor {
    @Accessor
    IntProvider getCount();
}
