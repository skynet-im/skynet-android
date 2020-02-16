package de.vectordata.skynet.net.packet;

import java.io.StreamCorruptedException;

import de.vectordata.skynet.crypto.Aes;
import de.vectordata.skynet.crypto.keys.ChannelKeys;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.DaystreamMessage;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.annotation.Channel;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageType;

@Channel(ChannelType.PROFILE_DATA)
public class P24DaystreamMessage extends ChannelMessagePacket {

    public MessageType messageType;
    public String text;

    @Override
    public void writeContents(PacketBuffer buffer, KeyProvider keyProvider) {
        ChannelKeys channelKeys = keyProvider.getChannelKeys(channelId);
        PacketBuffer encrypted = new PacketBuffer();
        encrypted.writeByte((byte) messageType.ordinal());
        encrypted.writeString(text, LengthPrefix.MEDIUM);
        Aes.encryptSigned(encrypted.toArray(), buffer, true, channelKeys);
    }

    @Override
    public void readContents(PacketBuffer buffer, KeyProvider keyProvider) {
        ChannelKeys channelKeys = keyProvider.getChannelKeys(channelId);
        try {
            PacketBuffer decrypted = new PacketBuffer(Aes.decryptSigned(buffer, 0, channelKeys));
            messageType = MessageType.values()[decrypted.readByte()];
            text = decrypted.readString(LengthPrefix.MEDIUM);
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        }
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
