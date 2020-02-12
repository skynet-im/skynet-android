package de.vectordata.skynet.net.packet;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.skynet.crypto.keys.KeyProvider;
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
public class P28BlockList extends ChannelMessagePacket {

    public List<Long> blockedAccounts = new ArrayList<>();
    public List<Long> blockedConversations = new ArrayList<>();

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeUInt16(blockedAccounts.size());
        for (long acc : blockedAccounts)
            buffer.writeInt64(acc);
        buffer.writeUInt16(blockedConversations.size());
        for (long conv : blockedConversations)
            buffer.writeInt64(conv);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        this.blockedAccounts.clear();
        this.blockedConversations.clear();

        int size = buffer.readUInt16();
        for (int i = 0; i < size; i++)
            blockedAccounts.add(buffer.readInt64());

        size = buffer.readUInt16();
        for (int i = 0; i < size; i++)
            blockedConversations.add(buffer.readInt64());
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x28;
    }

    @Override
    public void writeToDatabase(PacketDirection packetDirection) {
    }
}
