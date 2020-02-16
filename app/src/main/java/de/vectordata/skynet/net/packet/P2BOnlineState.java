package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.OnlineStateDb;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.OnlineState;
import de.vectordata.skynet.util.date.DateTime;

public class P2BOnlineState extends ChannelMessagePacket {

    public OnlineState onlineState;

    public DateTime lastActive;

    @Override
    public void writeContents(PacketBuffer buffer, KeyProvider keyProvider) {

    }

    @Override
    public void readContents(PacketBuffer buffer, KeyProvider keyProvider) {
        onlineState = OnlineState.values()[buffer.readByte()];
        if (onlineState == OnlineState.INACTIVE)
            lastActive = buffer.readDate();
    }

    @Override
    public void persistContents(PacketDirection direction) {
        Storage.getDatabase().onlineStateDao().insert(OnlineStateDb.fromPacket(this));
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
