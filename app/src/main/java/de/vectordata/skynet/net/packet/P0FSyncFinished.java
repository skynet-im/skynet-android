package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;

public class P0FSyncFinished implements Packet {
    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x0F;
    }
}
