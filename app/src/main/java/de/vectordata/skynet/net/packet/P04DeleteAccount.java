package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.net.PacketHandler;

public class P04DeleteAccount implements Packet {

    public byte[] keyHash;

    @Override
    public void writePacket(PacketBuffer buffer) {
        buffer.writeByteArray(keyHash, false);
    }

    @Override
    public void readPacket(PacketBuffer buffer) {
    }

    @Override
    public void handlePacket(PacketHandler handler) {
    }

    @Override
    public byte getId() {
        return 0x04;
    }
}
