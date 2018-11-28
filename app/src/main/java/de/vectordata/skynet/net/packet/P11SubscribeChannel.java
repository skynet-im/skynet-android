package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.net.PacketHandler;

public class P11SubscribeChannel implements Packet {

    public long channelId;
    public byte packetId;

    @Override
    public void writePacket(PacketBuffer buffer) {
        buffer.writeInt64(channelId);
        buffer.writeByte(packetId);
    }

    @Override
    public void readPacket(PacketBuffer buffer) {
    }

    @Override
    public void handlePacket(PacketHandler handler) {
    }

    @Override
    public byte getId() {
        return 0x11;
    }
}
