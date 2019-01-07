package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.packet.annotation.Channel;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageFlags;

@Flags(MessageFlags.LOOPBACK)
@Channel(ChannelType.DIRECT)
public class P1AVerifiedKeys extends ChannelMessagePacket {

    public byte[] sha256;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeByteArray(sha256, false);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        sha256 = buffer.readByteArray(32);
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x1A;
    }

    @Override
    public void writeToDatabase() {
    }
}
