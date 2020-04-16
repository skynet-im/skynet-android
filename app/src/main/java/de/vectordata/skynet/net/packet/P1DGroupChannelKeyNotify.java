package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.GroupChannelKeyNotify;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageFlags;

@Flags(MessageFlags.NO_SENDER_SYNC)
public class P1DGroupChannelKeyNotify extends ChannelMessagePacket {

    public long groupChannelId;
    public byte[] newKey;
    public byte[] historyKey;

    @Override
    public void writeContents(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeInt64(groupChannelId);
        buffer.writeByteArray(newKey, LengthPrefix.NONE);
        buffer.writeByteArray(historyKey, LengthPrefix.NONE);
    }

    @Override
    public void readContents(PacketBuffer buffer, KeyProvider keyProvider) {
        groupChannelId = buffer.readInt64();
        newKey = buffer.readBytes(64);
        historyKey = buffer.readBytes(64);
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x1D;
    }

    @Override
    public void persistContents(PacketDirection packetDirection) {
        if (packetDirection == PacketDirection.SEND && hasFlag(MessageFlags.NO_SENDER_SYNC))
            return;
        Storage.getDatabase().groupChannelKeyNotifyDao().insert(GroupChannelKeyNotify.fromPacket(this));
    }
}
