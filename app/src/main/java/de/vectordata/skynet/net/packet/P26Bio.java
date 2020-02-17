package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Bio;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;

public class P26Bio extends ChannelMessagePacket {

    public String bio;

    @Override
    public void writeContents(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeString(bio, LengthPrefix.MEDIUM);
    }

    @Override
    public void readContents(PacketBuffer buffer, KeyProvider keyProvider) {
        bio = buffer.readString(LengthPrefix.MEDIUM);
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x26;
    }

    @Override
    public void persistContents(PacketDirection packetDirection) {
        Storage.getDatabase().bioDao().insert(Bio.fromPacket(this));
    }
}
