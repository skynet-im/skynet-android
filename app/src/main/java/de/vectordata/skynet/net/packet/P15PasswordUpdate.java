package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.PasswordUpdate;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.annotation.Channel;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageFlags;

@Flags(MessageFlags.UNENCRYPTED)
@Channel(ChannelType.LOOPBACK)
public class P15PasswordUpdate extends ChannelMessagePacket {

    public byte[] loopbackKeyNotify;
    public byte[] keyHash;

    @Override
    public void writeContents(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeByteArray(loopbackKeyNotify, LengthPrefix.MEDIUM);
        buffer.writeByteArray(keyHash, LengthPrefix.NONE);
    }

    @Override
    public void readContents(PacketBuffer buffer, KeyProvider keyProvider) {
        keyHash = buffer.readBytes(32);
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x15;
    }

    @Override
    public void persistContents(PacketDirection packetDirection) {
        Storage.getDatabase().passwordUpdateDao().insert(PasswordUpdate.fromPacket(this));
    }
}
