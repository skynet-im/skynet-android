package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.CreateSessionError;

public class P07CreateSessionResponse implements Packet {

    public long accountId;
    public long sessionId;
    public CreateSessionError errorCode;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        accountId = buffer.readInt64();
        sessionId = buffer.readInt64();
        errorCode = CreateSessionError.values()[buffer.readByte()];
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
