package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;

public class P1BDirectChannelUpdate implements Packet {

    public long keyAccountId;
    public byte[] keyHash;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeInt64(keyAccountId);
        buffer.writeByteArray(keyHash, false);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        keyAccountId = buffer.readInt64();
        keyHash = buffer.readByteArray(32);
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x1B;
    }
}
