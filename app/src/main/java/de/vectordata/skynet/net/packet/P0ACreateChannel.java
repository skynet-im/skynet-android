package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.packet.base.AbstractPacket;
import de.vectordata.skynet.util.date.DateTime;

public class P0ACreateChannel extends AbstractPacket {

    public long channelId;
    public ChannelType channelType;
    public long ownerId;
    public DateTime creationTime;
    public long counterpartId;

    public P0ACreateChannel() {
    }

    public P0ACreateChannel(long channelId, ChannelType channelType, long counterpartId) {
        this.channelId = channelId;
        this.channelType = channelType;
        this.counterpartId = counterpartId;
    }

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeInt64(channelId);
        buffer.writeByte((byte) channelType.ordinal());
        if (channelType == ChannelType.DIRECT)
            buffer.writeInt64(counterpartId);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        channelId = buffer.readInt64();
        channelType = ChannelType.values()[buffer.readByte()];
        ownerId = buffer.readInt64();
        creationTime = buffer.readDate();
        if (channelType == ChannelType.DIRECT)
            counterpartId = buffer.readInt64();
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x0A;
    }
}
