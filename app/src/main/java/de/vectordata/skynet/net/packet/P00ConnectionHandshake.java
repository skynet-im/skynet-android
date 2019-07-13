package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.ConnectionState;
import de.vectordata.skynet.net.packet.annotation.AllowState;
import de.vectordata.skynet.net.packet.base.AbstractPacket;

@AllowState(ConnectionState.HANDSHAKING)
public class P00ConnectionHandshake extends AbstractPacket {

    public int protocolVersion;
    public String applicationIdentifier;
    public int versionCode;

    public P00ConnectionHandshake(int protocolVersion, String applicationIdentifier, int versionCode) {
        this.protocolVersion = protocolVersion;
        this.applicationIdentifier = applicationIdentifier;
        this.versionCode = versionCode;
    }

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeInt32(protocolVersion);
        buffer.writeString(applicationIdentifier);
        buffer.writeInt32(versionCode);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
    }

    @Override
    public void handlePacket(PacketHandler handler) {
    }

    @Override
    public byte getId() {
        return 0x00;
    }
}
