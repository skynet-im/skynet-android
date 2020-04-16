package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.packet.base.AbstractPacket;
import de.vectordata.skynet.net.packet.model.ChannelAction;
import de.vectordata.skynet.net.packet.model.OnlineState;

public class P34SetClientState extends AbstractPacket {

    public OnlineState onlineState;

    public ChannelAction channelAction;

    public long channelId;

    public P34SetClientState(OnlineState onlineState) {
        this.onlineState = onlineState;
        this.channelAction = ChannelAction.NONE;
    }

    public P34SetClientState(OnlineState onlineState, ChannelAction channelAction, long channelId) {
        this.onlineState = onlineState;
        this.channelAction = channelAction;
        this.channelId = channelId;
    }

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeByte((byte) onlineState.ordinal());
        buffer.writeByte((byte) channelAction.ordinal());
        if (channelAction != ChannelAction.NONE)
            buffer.writeInt64(channelId);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        onlineState = OnlineState.values()[buffer.readByte()];
        channelAction = ChannelAction.values()[buffer.readByte()];
        if (channelAction != ChannelAction.NONE)
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
