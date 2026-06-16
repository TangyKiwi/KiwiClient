package com.tangykiwi.kiwiclient.mixin.sodium;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.XRay;

import net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(value = LightDataAccess.class, remap = false)
public abstract class LightDataAccessMixin {
    @Shadow
	protected BlockAndTintGetter level;

	@Shadow
	@Final
	private BlockPos.MutableBlockPos pos;

	@ModifyArg(method = "compute", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/model/light/data/LightDataAccess;packBL(I)I"))
	private int compute_modifyBL(int blockLight) {
		XRay xray = (XRay) KiwiClient.moduleManager.getModule(XRay.class);
		if (xray.isEnabled()) {
			BlockState state = level.getBlockState(pos);
			if (xray.blocks.contains(state.getBlock())) {
				return 15;
			}
		}
		return blockLight;
	}
}
