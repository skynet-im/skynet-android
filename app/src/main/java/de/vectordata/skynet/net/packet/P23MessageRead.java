package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageFlags;

@Flags(MessageFlags.UNENCRYPTED)
public class P23MessageRead extends ChannelMessagePacket {

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
        return 0x23;
    }

    @Override
    public void persistContents(PacketDirection packetDirection) {
    }

    @Override
    public boolean validatePacket() {
        return dependencies.size() > 0;
    }
}
