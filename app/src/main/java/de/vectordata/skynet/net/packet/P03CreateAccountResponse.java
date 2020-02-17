package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.packet.base.AbstractPacket;
import de.vectordata.skynet.net.packet.model.CreateAccountStatus;

public class P03CreateAccountResponse extends AbstractPacket {

    public CreateAccountStatus statusCode;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        statusCode = CreateAccountStatus.values()[buffer.readByte()];
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x03;
    }
}
