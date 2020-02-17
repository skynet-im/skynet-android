package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.DaystreamMessage;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageType;

public class P24DaystreamMessage extends ChannelMessagePacket {

    public MessageType messageType;
    public String text;

    @Override
    public void writeContents(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeByte((byte) messageType.ordinal());
        buffer.writeString(text, LengthPrefix.MEDIUM);
    }

    @Override
    public void readContents(PacketBuffer buffer, KeyProvider keyProvider) {
        messageType = MessageType.values()[buffer.readByte()];
        text = buffer.readString(LengthPrefix.MEDIUM);
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x24;
    }

    @Override
    public void persistContents(PacketDirection packetDirection) {
        Storage.getDatabase().daystreamMessageDao().insert(DaystreamMessage.fromPacket(this));
    }
}
