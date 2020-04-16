package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageFlags;

@Flags(MessageFlags.LOOPBACK)
public class P2ABackgroundImage extends ChannelMessagePacket {

    @Override
    public void writeContents(PacketBuffer buffer, KeyProvider keyProvider) {

    }

    @Override
    public void readContents(PacketBuffer buffer, KeyProvider keyProvider) {

    }

    @Override
    public void persistContents(PacketDirection direction) {

    }

    @Override
    public void handlePacket(PacketHandler handler) {

    }

    @Override
    public byte getId() {
        return 0x2A;
    }
}
