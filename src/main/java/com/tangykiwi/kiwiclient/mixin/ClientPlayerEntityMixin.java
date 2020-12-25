package com.tangykiwi.kiwiclient.mixin;

import com.mojang.authlib.GameProfile;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.TickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    protected MinecraftClient client;

    @Shadow
    protected void autoJump(float float_1, float float_2) {
    }

    @Inject(method = "tick()V", at = @At("RETURN"), cancellable = true)
    public void tick(CallbackInfo info) {
        TickEvent event = new TickEvent();
        KiwiClient.eventBus.post(event);
        if (event.isCancelled())
            info.cancel();
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(String message, CallbackInfo callbackInfo) {
        if(message.equals(".d")) {
            ItemStack itemStack = new ItemStack(Items.WRITABLE_BOOK, 1);
            ListTag pages = new ListTag();
            pages.add(0, (Tag) StringTag.of((String)"DUPE"));
            itemStack.putSubTag("pages", (Tag) pages);
            itemStack.putSubTag("title", (Tag)StringTag.of((String)"a"));
            client.getNetworkHandler().sendPacket((Packet)new BookUpdateC2SPacket(itemStack, true, inventory.selectedSlot));
            callbackInfo.cancel();
        }
    }
}
