package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.StorageAccess;
import de.vectordata.skynet.data.model.MailAddress;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.packet.annotation.Channel;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageFlags;

@Flags(MessageFlags.UNENCRYPTED)
@Channel(ChannelType.LOOPBACK)
public class P14MailAddress extends ChannelMessagePacket {

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

    @Override
    public void writeToDatabase() {
        StorageAccess.getDatabase().mailAddressDao().insert(MailAddress.fromPacket(this));
    }
}
