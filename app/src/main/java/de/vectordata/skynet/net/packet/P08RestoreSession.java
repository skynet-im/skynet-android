package de.vectordata.skynet.net.packet;

import java.util.List;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.ConnectionState;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.packet.annotation.AllowState;
import de.vectordata.skynet.net.packet.base.Packet;

@AllowState(ConnectionState.AUTHENTICATING)
public class P08RestoreSession implements Packet {

    public long accountId;
    public byte[] keyHash;
    public long sessionId;
    public List<ChannelItem> channels;

    public P08RestoreSession(long accountId, byte[] keyHash, long sessionId, List<ChannelItem> channels) {
        this.accountId = accountId;
        this.keyHash = keyHash;
        this.sessionId = sessionId;
        this.channels = channels;
    }

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
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
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
    }

    @Override
    public void handlePacket(PacketHandler handler) {
    }

    @Override
    public byte getId() {
        return 0x08;
    }

    public static class ChannelItem {
        public long channelId;
        public long lastMessageId;

        public ChannelItem(long channelId, long lastMessageId) {
            this.channelId = channelId;
            this.lastMessageId = lastMessageId;
        }
    }
}
