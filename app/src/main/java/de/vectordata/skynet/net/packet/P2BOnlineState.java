package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.libjvsl.util.cscompat.DateTime;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.packet.base.AbstractPacket;
import de.vectordata.skynet.net.packet.model.OnlineState;

public class P2BOnlineState extends AbstractPacket {

    public OnlineState onlineState;

    public DateTime lastActive;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeByte((byte) onlineState.ordinal());
        if (onlineState == OnlineState.INACTIVE)
            buffer.writeDate(lastActive);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        onlineState = OnlineState.values()[buffer.readByte()];
        if (onlineState == OnlineState.INACTIVE)
            lastActive = buffer.readDate();
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
