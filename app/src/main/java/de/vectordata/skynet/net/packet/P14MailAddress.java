package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.MailAddress;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageFlags;

@Flags(MessageFlags.UNENCRYPTED)
public class P14MailAddress extends ChannelMessagePacket {

    public String mailAddress;

    @Override
    public void writeContents(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeString(mailAddress, LengthPrefix.SHORT);
    }

    @Override
    public void readContents(PacketBuffer buffer, KeyProvider keyProvider) {
        mailAddress = buffer.readString(LengthPrefix.SHORT);
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
    public void persistContents(PacketDirection packetDirection) {
        Storage.getDatabase().mailAddressDao().insert(MailAddress.fromPacket(this));
    }
}
