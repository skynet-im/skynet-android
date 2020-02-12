package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.PasswordUpdate;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.annotation.Channel;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageFlags;

@Flags(MessageFlags.UNENCRYPTED)
@Channel(ChannelType.LOOPBACK)
public class P15PasswordUpdate extends ChannelMessagePacket {

    public byte[] oldKeyHash;
    public byte[] keyHash;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeByteArray(oldKeyHash, false);
        buffer.writeByteArray(keyHash, false);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        keyHash = buffer.readByteArray(32);
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
    public void writeToDatabase(PacketDirection packetDirection) {
        Storage.getDatabase().passwordUpdateDao().insert(PasswordUpdate.fromPacket(this));
    }
}
