package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.packet.base.AbstractPacket;
import de.vectordata.skynet.net.packet.model.ChannelAction;

public class P2CChannelAction extends AbstractPacket {

    public long channelId;

    public long accountId;

    public ChannelAction channelAction;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeInt64(channelId);
        buffer.writeInt64(accountId);
        buffer.writeByte((byte) channelAction.ordinal());
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        this.channelId = buffer.readInt64();
        this.accountId = buffer.readInt64();
        this.channelAction = ChannelAction.values()[buffer.readByte()];
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x2C;
    }
}
