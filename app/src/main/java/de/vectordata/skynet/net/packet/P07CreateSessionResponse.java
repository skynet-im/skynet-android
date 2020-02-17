package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.packet.base.AbstractPacket;
import de.vectordata.skynet.net.packet.model.CreateSessionStatus;

public class P07CreateSessionResponse extends AbstractPacket {

    public CreateSessionStatus statusCode;
    public long accountId;
    public long sessionId;
    public byte[] sessionToken;
    public String webToken;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        statusCode = CreateSessionStatus.values()[buffer.readByte()];
        accountId = buffer.readInt64();
        sessionId = buffer.readInt64();
        sessionToken = buffer.readBytes(32);
        webToken = buffer.readString(LengthPrefix.MEDIUM);
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x07;
    }
}
