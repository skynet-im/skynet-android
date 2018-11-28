package de.vectordata.skynet.net.packet;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.net.PacketHandler;

public class P08RestoreSession implements Packet {

    public long accountId;
    public byte[] keyHash;
    public long sessionId;
    public List<ChannelItem> channels = new ArrayList<>();

    @Override
    public void writePacket(PacketBuffer buffer) {
        buffer.writeInt64(accountId);
        buffer.writeByteArray(keyHash, false);
        buffer.writeInt64(sessionId);
        buffer.writeInt16((short) channels.size());
        for (ChannelItem item : channels) {
            buffer.writeInt64(item.channelId);
            buffer.writeInt64(item.lastMessageId);
        }
    }

    @Override
    public void readPacket(PacketBuffer buffer) {
    }

    @Override
    public void handlePacket(PacketHandler handler) {
    }

    @Override
    public byte getId() {
        return 0x08;
    }

    public class ChannelItem {
        public long channelId;
        public long lastMessageId;

        public ChannelItem(long channelId, long lastMessageId) {
            this.channelId = channelId;
            this.lastMessageId = lastMessageId;
        }
    }
}
