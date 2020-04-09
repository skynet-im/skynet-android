package de.vectordata.skynet.net.packet;

import java.util.List;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.ConnectionState;
import de.vectordata.skynet.net.packet.annotation.AllowState;
import de.vectordata.skynet.net.packet.base.AbstractPacket;

@AllowState(ConnectionState.AUTHENTICATING)
public class P08RestoreSession extends AbstractPacket {

    public long sessionId;
    public byte[] sessionToken;
    public long lastMessageId;
    public List<Long> channels;

    public P08RestoreSession(long sessionId, byte[] sessionToken, long lastMessageId, List<Long> channels) {
        this.sessionId = sessionId;
        this.sessionToken = sessionToken;
        this.lastMessageId = lastMessageId;
        this.channels = channels;
    }

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeInt64(sessionId);
        buffer.writeByteArray(sessionToken, LengthPrefix.NONE);
        buffer.writeInt64(lastMessageId);

        buffer.writeUInt16(channels.size());
        for (long channelId : channels) {
            buffer.writeInt64(channelId);
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
}
