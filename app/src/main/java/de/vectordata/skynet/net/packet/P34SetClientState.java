package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.packet.base.AbstractPacket;
import de.vectordata.skynet.net.packet.model.ChannelAction;
import de.vectordata.skynet.net.packet.model.OnlineState;

public class P34SetClientState extends AbstractPacket {

    public OnlineState onlineState;

    public ChannelAction channelAction;

    public long channelId;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeByte((byte) onlineState.ordinal());
        buffer.writeByte((byte) channelAction.ordinal());
        buffer.writeInt64(channelId);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        onlineState = OnlineState.values()[buffer.readByte()];
        channelAction = ChannelAction.values()[buffer.readByte()];
        channelId = buffer.readInt64();
    }

    @Override
    public void handlePacket(PacketHandler handler) {
    }

    @Override
    public byte getId() {
        return 0x34;
    }
}
