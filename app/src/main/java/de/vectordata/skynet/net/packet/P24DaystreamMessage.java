package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.crypt.AesStatic;
import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.crypto.keys.KeyStore;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.DaystreamMessage;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.annotation.Channel;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageType;

@Channel(ChannelType.PROFILE_DATA)
public class P24DaystreamMessage extends ChannelMessagePacket {

    public MessageType messageType;
    public String text;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        KeyStore keyStore = keyProvider.getMessageKeys(getParent());
        PacketBuffer encrypted = new PacketBuffer();
        encrypted.writeByte((byte) messageType.ordinal());
        encrypted.writeString(text);
        AesStatic.encryptWithHmac(encrypted.toArray(), buffer, true, keyStore.getHmacKey(), keyStore.getAesKey());
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        KeyStore keyStore = keyProvider.getMessageKeys(getParent());
        PacketBuffer decrypted = new PacketBuffer(AesStatic.decryptWithHmac(buffer, 0, keyStore.getHmacKey(), keyStore.getAesKey()));
        messageType = MessageType.values()[decrypted.readByte()];
        text = decrypted.readString();
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
    public void writeToDatabase(PacketDirection packetDirection) {
        Storage.getDatabase().daystreamMessageDao().insert(DaystreamMessage.fromPacket(this));
    }
}
