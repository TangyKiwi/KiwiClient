package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.command.commands.Dupe;
import com.tangykiwi.kiwiclient.event.ReceivePacketEvent;
import com.tangykiwi.kiwiclient.event.SendPacketEvent;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Shadow
    private void sendImmediately(Packet<?> packet_1, GenericFutureListener<? extends Future<? super Void>> genericFutureListener_1) {
    }

    @Inject(method = "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At("HEAD"), cancellable = true)
    public void send(Packet<?> packet_1, GenericFutureListener<? extends Future<? super Void>> genericFutureListener_1, CallbackInfo callbackInfo) {
        SendPacketEvent event = new SendPacketEvent(packet_1);
        KiwiClient.eventBus.post(event);

        if (event.isCancelled()) callbackInfo.cancel();
    }

    @Inject(
        method = {"send(Lnet/minecraft/network/Packet;)V"},
        at = {@At("TAIL")},
        cancellable = true
    )
    private void send(Packet<?> packet, CallbackInfo ci) {
        Dupe.packetSent(packet);
    }

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void onHandlePacket(Packet<T> packet, PacketListener listener, CallbackInfo info) {
        ReceivePacketEvent event = new ReceivePacketEvent(packet);
        KiwiClient.eventBus.post(event);

        if (event.isCancelled()) info.cancel();
    }
}
