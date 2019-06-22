package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.crypt.AesStatic;
import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.crypto.keys.KeyStore;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.data.model.enums.MessageState;
import de.vectordata.skynet.net.PacketHandler;
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
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        KeyStore keyStore = keyProvider.getChannelKeys(getParent().channelId);
        PacketBuffer encrypted = new PacketBuffer();
        encrypted.writeByte((byte) messageType.ordinal());
        encrypted.writeString(text);
        encrypted.writeInt64(quotedMessage);
        AesStatic.encryptWithHmac(encrypted.toArray(), buffer, true, keyStore.getHmacKey(), keyStore.getAesKey());
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        KeyStore keyStore = keyProvider.getChannelKeys(getParent().channelId);
        PacketBuffer decrypted = new PacketBuffer(AesStatic.decryptWithHmac(buffer, 0, keyStore.getHmacKey(), keyStore.getAesKey()));
        messageType = MessageType.values()[decrypted.readByte()];
        text = decrypted.readString();
        quotedMessage = decrypted.readInt64();
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
    public void writeToDatabase(PacketDirection packetDirection) {
        boolean isUnread = getParent().senderId != Storage.getSession().getAccountId() && packetDirection == PacketDirection.RECEIVE;
        Storage.getDatabase().chatMessageDao().insert(ChatMessage.fromPacket(this, packetDirection == PacketDirection.RECEIVE ? MessageState.NONE : MessageState.SENDING, isUnread));
    }
}
