package de.vectordata.skynet.net;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.net.packet.P01ConnectionResponse;
import de.vectordata.skynet.net.packet.Packet;

public class PacketHandler {

    private static final Packet[] REGISTERED_PACKETS = new Packet[]{
            null,
            new P01ConnectionResponse()
    };

    void handlePacket(byte id, byte[] payload) {
        int uId = id & 0xFF;
        if (uId >= REGISTERED_PACKETS.length)
            return;

        Packet packet = REGISTERED_PACKETS[id];
        if (packet == null)
            return;

        PacketBuffer buffer = new PacketBuffer(payload);
        packet.readPacket(buffer);
        packet.handlePacket(this);
    }

    public void handlePacket(P01ConnectionResponse packet) {

    }
}
