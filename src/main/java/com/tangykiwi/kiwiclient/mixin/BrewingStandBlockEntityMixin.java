package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(BrewingStandBlockEntity.class)
public abstract class BrewingStandBlockEntityMixin extends LockableContainerBlockEntity {


    @Shadow private DefaultedList<ItemStack> inventory;
    public ItemStack item1;
    public ItemStack item2;
    public ItemStack item3;
    Boolean sendData = true;

    private BrewingStandBlockEntityMixin(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);

    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo ci) {
        item1 = inventory.get(0);
        item2 = inventory.get(1);
        item3 = inventory.get(2);

        if (!this.world.isClient) {
            Stream<PlayerEntity> watchingPlayers = PlayerStream.watching(world, getPos());
            PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
            passedData.writeBlockPos(pos);
            passedData.writeItemStack(inventory.get(0));
            passedData.writeItemStack(inventory.get(1));
            passedData.writeItemStack(inventory.get(2));
            passedData.writeItemStack(inventory.get(3));
            passedData.writeItemStack(inventory.get(4));

            passedData.writeString(String.valueOf(inventory));
            watchingPlayers.forEach(player -> ServerSidePacketRegistryImpl.INSTANCE.sendToPlayer(player, KiwiClient.POTION_BOTTLES, passedData));
            sendData = false;
        }
    }

    @Inject(at = @At("RETURN"), method = "getStack", cancellable = true)
    public void getStack(int slot, CallbackInfoReturnable cir) {
        this.sendData = true;
    }
}
