package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.crypto.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.OnlineState;
import de.vectordata.skynet.net.packet.base.RealtimeMessagePacket;

public class P2BOnlineState extends RealtimeMessagePacket {

    public OnlineState onlineState;
    public DateTime lastActive;
    public long writingToChannelId;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeByte((byte) onlineState.ordinal());
        buffer.writeDate(lastActive);
        buffer.writeInt64(writingToChannelId);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        onlineState = OnlineState.values()[buffer.readByte()];
        lastActive = buffer.readDate();
        writingToChannelId = buffer.readInt64();
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x2B;
    }
}
