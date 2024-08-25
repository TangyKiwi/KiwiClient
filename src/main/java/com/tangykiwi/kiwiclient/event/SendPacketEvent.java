package com.tangykiwi.kiwiclient.event;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;

public class SendPacketEvent extends Event {
    public Packet<?> packet;
    public ClientConnection connection;

    public SendPacketEvent(Packet<?> packet, ClientConnection connection) {
        this.packet = packet;
        this.connection = connection;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}

