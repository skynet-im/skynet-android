package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.ConnectionState;
import de.vectordata.skynet.net.packet.base.Packet;

public class P01ConnectionResponse implements Packet {

    public ConnectionState connectionState;
    public int latestVersionCode;
    public String latestVersion;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        connectionState = ConnectionState.values()[buffer.readByte()];
        latestVersionCode = buffer.readInt32();
        latestVersion = buffer.readString();
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x01;
    }
}
