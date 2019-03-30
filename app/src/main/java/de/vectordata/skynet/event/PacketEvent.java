package de.vectordata.skynet.event;

import de.vectordata.skynet.net.packet.base.Packet;

public class PacketEvent {

    private Packet packet;

    public PacketEvent(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }

}
