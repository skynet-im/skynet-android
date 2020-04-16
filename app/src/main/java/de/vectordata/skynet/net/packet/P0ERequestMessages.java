package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.packet.base.AbstractPacket;

public class P0ERequestMessages extends AbstractPacket {

    public long channelId;
    public long after;
    public long before;
    public int maxCount;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeInt64(channelId);
        buffer.writeInt64(after);
        buffer.writeInt64(before);
        buffer.writeUInt16(maxCount);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
    }

    @Override
    public void handlePacket(PacketHandler handler) {
    }

    @Override
    public byte getId() {
        return 0x0E;
    }
}
