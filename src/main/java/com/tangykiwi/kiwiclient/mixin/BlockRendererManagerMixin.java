package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.ModuleManager;
import com.tangykiwi.kiwiclient.modules.render.XRay;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.block.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockRenderManager.class)
public class BlockRendererManagerMixin {
    @Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true)
    private void renderBlock_head(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, Random random, CallbackInfoReturnable<Boolean> ci) {
        XRay xray = (XRay) KiwiClient.moduleManager.getModule(XRay.class);

        if (xray.isEnabled() && !xray.isVisible(state.getBlock())) {
            if (xray.getSetting(1).asToggle().state) {
                if (xray.getSetting(1).asToggle().getChild(1).asToggle().state
                        && (state.getBlock() instanceof FernBlock
                        || state.getBlock() instanceof PlantBlock
                        || state.getBlock() instanceof RailBlock
                        || state.getBlock().getClass() == TallPlantBlock.class
                        || getTopBlockIgnoreLeaves(pos.getX(), pos.getZ()) == pos.getY())) {
                    ci.setReturnValue(false);
                    return;
                }

                vertexConsumer.fixedColor(-1, -1, -1, (int) xray.getSetting(1).asToggle().getChild(0).asSlider().getValue());
            } else {
                ci.setReturnValue(false);
            }
        }
    }

    @Inject(method = "renderBlock", at = @At("RETURN"))
    private void renderBlock_return(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, Random random, CallbackInfoReturnable<Boolean> ci) {
        vertexConsumer.unfixColor();
    }

    private int getTopBlockIgnoreLeaves(int x, int z) {
        int top = Utils.mc.world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z) - 1;

        while (top > Utils.mc.world.getBottomY()) {
            BlockState state = Utils.mc.world.getBlockState(new BlockPos(x, top, z));

            if (!(state.isAir() || state.getBlock() instanceof LeavesBlock || state.getBlock() instanceof PlantBlock)) {
                break;
            }

            top--;
        }

        return top;
    }
}
