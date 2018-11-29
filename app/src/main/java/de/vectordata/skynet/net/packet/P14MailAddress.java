package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.KeyProvider;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.model.MessageFlags;
import de.vectordata.skynet.net.packet.annotation.ChannelMessage;
import de.vectordata.skynet.net.packet.annotation.Flags;

@Flags(MessageFlags.UNENCRYPTED)
@ChannelMessage(ChannelType.LOOPBACK)
public class P14MailAddress implements Packet {

    public String mailAddress;

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeString(mailAddress);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        mailAddress = buffer.readString();
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x14;
    }
}
