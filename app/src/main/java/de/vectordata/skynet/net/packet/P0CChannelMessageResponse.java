package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.crypto.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.MessageSendError;
import de.vectordata.skynet.net.packet.base.Packet;

public class P0CChannelMessageResponse implements Packet {

    public long channelId;
    public long tempMessageId;
    public MessageSendError errorCode;
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
        errorCode = MessageSendError.values()[buffer.readByte()];
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
