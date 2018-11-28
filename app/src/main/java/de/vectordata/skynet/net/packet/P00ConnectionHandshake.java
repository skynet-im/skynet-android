package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.net.PacketHandler;

public class P00ConnectionHandshake implements Packet {

    public int protocolVersion;
    public String applicationIdentifier;
    public int versionCode;

    @Override
    public void writePacket(PacketBuffer buffer) {
        buffer.writeInt32(protocolVersion);
        buffer.writeString(applicationIdentifier);
        buffer.writeInt32(versionCode);
    }

    @Override
    public void readPacket(PacketBuffer buffer) {
    }

    @Override
    public void handlePacket(PacketHandler handler) {
    }

    @Override
    public byte getId() {
        return 0x00;
    }
}
