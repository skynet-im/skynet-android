package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.packet.base.AbstractPacket;
import de.vectordata.skynet.net.packet.model.MessageSendStatus;
import de.vectordata.skynet.util.date.DateTime;

public class P0CChannelMessageResponse extends AbstractPacket {

    public long channelId;
    public long tempMessageId;
    public MessageSendStatus statusCode;
    public long messageId;
    public long skipCount;
    public DateTime dispatchTime;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        channelId = buffer.readInt64();
        tempMessageId = buffer.readInt64();
        statusCode = MessageSendStatus.values()[buffer.readByte()];
        messageId = buffer.readInt64();
        skipCount = buffer.readInt64();
        dispatchTime = buffer.readDate();
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x0C;
    }
}
