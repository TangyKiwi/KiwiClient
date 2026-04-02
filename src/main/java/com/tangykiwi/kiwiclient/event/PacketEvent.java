package com.tangykiwi.kiwiclient.event;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;

public class PacketEvent extends Event{
    public static class Receive extends PacketEvent {
        public Packet<?> packet;
        public Connection connection;

        public Receive(Packet<?> packet, Connection connection) {
            this.setCancelled(false);
            this.packet = packet;
            this.connection = connection;
        }
    }

    public static class Send extends PacketEvent {
        public Packet<?> packet;
        public Connection connection;

        public Send(Packet<?> packet, Connection connection) {
            this.setCancelled(false);
            this.packet = packet;
            this.connection = connection;
        }
    }

    public static class Sent {
        public Packet<?> packet;
        public Connection connection;

        public Sent(Packet<?> packet, Connection connection) {
            this.packet = packet;
            this.connection = connection;
        }
    }
}
