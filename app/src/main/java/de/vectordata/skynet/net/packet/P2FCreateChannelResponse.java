package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.packet.base.AbstractPacket;
import de.vectordata.skynet.net.packet.model.CreateChannelStatus;
import de.vectordata.skynet.util.date.DateTime;

public class P2FCreateChannelResponse extends AbstractPacket {

    public long tempChannelId;
    public CreateChannelStatus statusCode;
    public long channelId;
    public DateTime creationTime;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        tempChannelId = buffer.readInt64();
        statusCode = CreateChannelStatus.values()[buffer.readByte()];
        channelId = buffer.readInt64();
        creationTime = buffer.readDate();
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x2F;
    }
}
