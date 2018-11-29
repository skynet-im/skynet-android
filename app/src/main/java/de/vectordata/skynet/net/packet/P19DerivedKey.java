package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;

public class P19DerivedKey implements Packet {

    public byte[] key;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeByteArray(key, true);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        key = buffer.readByteArray();
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x19;
    }
}
