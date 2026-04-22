package com.tangykiwi.kiwiclient.mixin;

import java.util.Iterator;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.PacketEvent;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.TimeoutException;
import net.minecraft.network.Connection;
import net.minecraft.network.SkipPacketEncoderException;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;

@Mixin(value = Connection.class, priority = 1010)
public class ConnectionMixin {
    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void onHandlePacket(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof ClientboundBundlePacket bundle) {
            for (Iterator<Packet<? super ClientGamePacketListener>> it = bundle.subPackets().iterator(); it.hasNext(); ) {
                PacketEvent.Receive event = new PacketEvent.Receive(it.next(), (Connection) (Object) this);
                KiwiClient.eventBus.post(event);
                if (event.isCancelled()) it.remove();
            }
        } else {
            PacketEvent.Receive event = new PacketEvent.Receive(packet, (Connection) (Object) this);
            KiwiClient.eventBus.post(event);
            if (event.isCancelled()) ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "send(Lnet/minecraft/network/packet/Packet;Lio/netty/channel/ChannelFutureListener;)V", cancellable = true)
    private void onSendPacketHead(Packet<?> packet, @Nullable ChannelFutureListener channelFutureListener, CallbackInfo ci) {
        PacketEvent.Send event = new PacketEvent.Send(packet, (Connection) (Object) this);
        KiwiClient.eventBus.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lio/netty/channel/ChannelFutureListener;)V", at = @At("TAIL"))
    private void onSendPacketTail(Packet<?> packet, @Nullable ChannelFutureListener channelFutureListener, CallbackInfo ci) {
        KiwiClient.eventBus.post(new PacketEvent.Sent(packet, (Connection) (Object) this));
    }

    @Inject(method = "exceptionCaught", at = @At("HEAD"), cancellable = true)
    private void exceptionCaught(ChannelHandlerContext context, Throwable throwable, CallbackInfo ci) {
        if (!(throwable instanceof TimeoutException) && !(throwable instanceof SkipPacketEncoderException)) {
            ci.cancel();
        }
    }
}
