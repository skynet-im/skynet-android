package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;

public class P19ArchiveChannel extends ChannelMessagePacket {

    @Override
    public void writeContents(PacketBuffer buffer, KeyProvider keyProvider) {
    }

    @Override
    public void readContents(PacketBuffer buffer, KeyProvider keyProvider) {
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x19;
    }

    @Override
    public void persistContents(PacketDirection packetDirection) {
    }
}
