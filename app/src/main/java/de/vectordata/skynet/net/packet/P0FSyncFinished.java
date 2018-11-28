package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.net.PacketHandler;

public class P0FSyncFinished implements Packet {
    @Override
    public void writePacket(PacketBuffer buffer) {
    }

    @Override
    public void readPacket(PacketBuffer buffer) {
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
