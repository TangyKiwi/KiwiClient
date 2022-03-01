package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.modules.render.seedray.SeedRay;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DimensionType.class)
public class DimensionTypeMixin implements SeedRay.DimensionTypeCaller {
    @Shadow
    @Final
    private TagKey<Block> infiniburn;

    @Override public TagKey<Block> getInfiniburn() {
        return this.infiniburn;
    }

}
