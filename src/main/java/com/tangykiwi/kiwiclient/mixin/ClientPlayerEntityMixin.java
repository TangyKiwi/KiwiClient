package com.tangykiwi.kiwiclient.mixin;

import com.mojang.authlib.GameProfile;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.command.CommandManager;
import com.tangykiwi.kiwiclient.event.OnMoveEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.mixininterface.IClientPlayerEntity;
import com.tangykiwi.kiwiclient.modules.movement.FastBridge;
import com.tangykiwi.kiwiclient.modules.movement.SafeWalk;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements IClientPlayerEntity {
    @Shadow
    protected MinecraftClient client;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile gameProfile)
    {
        super(world, gameProfile);
    }

    @Inject(method = "tick()V", at = @At("RETURN"), cancellable = true)
    public void tick(CallbackInfo info) {
        TickEvent event = new TickEvent();
        KiwiClient.eventBus.post(event);
        if (event.isCancelled())
            info.cancel();
    }

    @Inject(at = {@At("HEAD")},
            method = {
                    "move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"})
    private void onMove(MovementType type, Vec3d offset, CallbackInfo callbackInfo)
    {
        OnMoveEvent event = new OnMoveEvent(this);
        KiwiClient.eventBus.register(event);
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(String message, CallbackInfo callbackInfo) {
        if(message.equals(".d")) {
            ItemStack itemStack = new ItemStack(Items.WRITABLE_BOOK, 1);
            ListTag pages = new ListTag();
            pages.add(0, (Tag) StringTag.of((String)"DUPE"));
            itemStack.putSubTag("pages", (Tag) pages);
            itemStack.putSubTag("title", (Tag)StringTag.of((String)"a"));
            client.getNetworkHandler().sendPacket((Packet) new BookUpdateC2SPacket(itemStack, true, client.player.inventory.selectedSlot));
            callbackInfo.cancel();
        }
        else if(message.startsWith(".say ")) {
            this.client.getNetworkHandler().sendPacket(new ChatMessageC2SPacket(message.substring(5)));
            callbackInfo.cancel();
        }
        else if(message.startsWith(Command.PREFIX)) {
            CommandManager.callCommand(message.substring(Command.PREFIX.length()));
            callbackInfo.cancel();
        }
    }

    @Override
    public void setNoClip(boolean noClip)
    {
        this.noClip = noClip;
    }

    @Override
    protected boolean clipAtLedge() {
        return super.clipAtLedge() || KiwiClient.moduleManager.getModule(SafeWalk.class).isEnabled();
    }
}
