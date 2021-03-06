package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.packet.base.AbstractPacket;
import de.vectordata.skynet.net.packet.model.HandshakeState;

public class P01ConnectionResponse extends AbstractPacket {

    public HandshakeState handshakeState;
    public int latestVersionCode;
    public String latestVersion;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        handshakeState = HandshakeState.values()[buffer.readByte()];
        if (handshakeState != HandshakeState.VALID) {
            latestVersionCode = buffer.readInt32();
            latestVersion = buffer.readString(LengthPrefix.SHORT);
        }
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
