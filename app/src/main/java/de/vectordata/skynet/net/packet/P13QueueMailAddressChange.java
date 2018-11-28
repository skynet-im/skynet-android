package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.MessageFlags;
import de.vectordata.skynet.net.packet.annotation.ChannelMessage;
import de.vectordata.skynet.net.packet.annotation.Flags;

@Flags(MessageFlags.UNENCRYPTED)
@ChannelMessage(ChannelType.LOOPBACK)
public class P13QueueMailAddressChange implements Packet {

    public String newMailAddress;

    @Override
    public void writePacket(PacketBuffer buffer) {
        buffer.writeString(newMailAddress);
    }

    @Override
    public void readPacket(PacketBuffer buffer) {
        newMailAddress = buffer.readString();
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x13;
    }
}
