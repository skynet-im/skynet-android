package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.crypt.AesStatic;
import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.crypto.keys.KeyStore;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.MessageType;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;

public class P20ChatMessage extends ChannelMessagePacket {

    public MessageType messageType;
    public String text;
    public long quotedMessage;

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
}
