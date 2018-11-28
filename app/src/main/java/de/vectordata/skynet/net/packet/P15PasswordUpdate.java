package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.MessageFlags;
import de.vectordata.skynet.net.packet.annotation.ChannelMessage;
import de.vectordata.skynet.net.packet.annotation.Flags;

@Flags(MessageFlags.UNENCRYPTED)
@ChannelMessage(ChannelType.LOOPBACK)
public class P15PasswordUpdate implements Packet {

    public byte[] oldKeyHash;
    public byte[] keyHash;

    @Override
    public void writePacket(PacketBuffer buffer) {
        buffer.writeByteArray(oldKeyHash, false);
        buffer.writeByteArray(keyHash, false);
    }

    @Override
    public void readPacket(PacketBuffer buffer) {
        keyHash = buffer.readByteArray(32);
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x15;
    }
}
