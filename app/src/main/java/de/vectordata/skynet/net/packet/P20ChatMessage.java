package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.data.model.enums.MessageState;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageType;

public class P20ChatMessage extends ChannelMessagePacket {

    public MessageType messageType;
    public String text;
    public long quotedMessage;

    public P20ChatMessage(MessageType messageType, String text, long quotedMessage) {
        this.messageType = messageType;
        this.text = text;
        this.quotedMessage = quotedMessage;
    }

    public P20ChatMessage() {
    }

    @Override
    public void writeContents(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeByte((byte) messageType.ordinal());
        buffer.writeString(text, LengthPrefix.MEDIUM);
        buffer.writeInt64(quotedMessage);
    }

    @Override
    public void readContents(PacketBuffer buffer, KeyProvider keyProvider) {
        messageType = MessageType.values()[buffer.readByte()];
        text = buffer.readString(LengthPrefix.MEDIUM).trim();
        quotedMessage = buffer.readInt64();
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x20;
    }

    @Override
    public void persistContents(PacketDirection packetDirection) {
        if (isSentByMe()) {
            Storage.getDatabase().chatMessageDao().insert(ChatMessage.fromPacket(this, packetDirection == PacketDirection.RECEIVE ? MessageState.SENT : MessageState.SENDING, false));
        } else {
            Storage.getDatabase().chatMessageDao().insert(ChatMessage.fromPacket(this, MessageState.NONE, true));
        }
    }
}
