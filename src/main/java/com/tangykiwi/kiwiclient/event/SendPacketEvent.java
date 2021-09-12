package com.tangykiwi.kiwiclient.event;

import net.minecraft.network.Packet;

public class SendPacketEvent extends Event {
    public Packet<?> packet;

    public SendPacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}

