package com.tangykiwi.kiwiclient.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.IsFullCubeEvent;
import com.tangykiwi.kiwiclient.event.RenderBlockEvent;
import com.tangykiwi.kiwiclient.modules.render.XRay;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlockState.class)
public class AbstractBlockStateMixin extends State<Block, BlockState> {

    private AbstractBlockStateMixin(Block object,
                                    ImmutableMap<Property<?>, Comparable<?>> immutableMap,
                                    MapCodec<BlockState> mapCodec)
    {
        super(object, immutableMap, mapCodec);
    }

    @Inject(at = {@At("TAIL")},
            method = {
                    "isFullCube(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z"},
            cancellable = true)
    private void onIsFullCube(BlockView world, BlockPos pos,
                              CallbackInfoReturnable<Boolean> callbackInfoReturnable)
    {
        IsFullCubeEvent event = new IsFullCubeEvent();
        KiwiClient.eventBus.register(event);

        callbackInfoReturnable.setReturnValue(callbackInfoReturnable.getReturnValue() && !event.isCancelled());
    }

    @Inject(method = "isOpaque", at = @At("HEAD"), cancellable = true)
    public void isOpaque(CallbackInfoReturnable<Boolean> callback) {
        RenderBlockEvent.Opaque event = new RenderBlockEvent.Opaque((BlockState) (Object) this);
        KiwiClient.eventBus.post(event);

        if (event.isOpaque() != null)
            callback.setReturnValue(event.isOpaque());
    }
}
