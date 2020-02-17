package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageFlags;

@Flags(MessageFlags.LOOPBACK | MessageFlags.UNENCRYPTED)
public class P13QueueMailAddressChange extends ChannelMessagePacket {

    public String newMailAddress;

    @Override
    public void writeContents(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeString(newMailAddress, LengthPrefix.SHORT);
    }

    @Override
    public void readContents(PacketBuffer buffer, KeyProvider keyProvider) {
        newMailAddress = buffer.readString(LengthPrefix.SHORT);
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x13;
    }

    @Override
    public void persistContents(PacketDirection packetDirection) {
    }
}
