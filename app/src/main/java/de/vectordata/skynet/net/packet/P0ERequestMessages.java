package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.net.PacketHandler;

public class P0ERequestMessages implements Packet {

    public long channelId;
    public long firstKnownMessageId;
    public long requestCount;

    @Override
    public void writePacket(PacketBuffer buffer) {
        buffer.writeInt64(channelId);
        buffer.writeInt64(firstKnownMessageId);
        buffer.writeInt64(requestCount);
    }

    @Override
    public void readPacket(PacketBuffer buffer) {
    }

    @Override
    public void handlePacket(PacketHandler handler) {
    }

    @Override
    public byte getId() {
        return 0x0E;
    }
}
