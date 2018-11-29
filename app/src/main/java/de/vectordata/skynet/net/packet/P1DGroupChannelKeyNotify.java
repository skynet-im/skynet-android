package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.MessageFlags;
import de.vectordata.skynet.net.packet.annotation.Flags;

@Flags(MessageFlags.NO_SENDER_SYNC)
public class P1DGroupChannelKeyNotify implements Packet {

    public long channelId;
    public byte[] newKey;
    public byte[] historyKey;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeInt64(channelId);
        buffer.writeByteArray(newKey, true);
        buffer.writeByteArray(historyKey, true);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        channelId = buffer.readInt64();
        newKey = buffer.readByteArray();
        historyKey = buffer.readByteArray();
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x1D;
    }
}