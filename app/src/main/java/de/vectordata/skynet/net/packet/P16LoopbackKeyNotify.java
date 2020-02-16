package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.LoopbackKeyNotify;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.annotation.Channel;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageFlags;

@Flags(MessageFlags.UNENCRYPTED)
@Channel(ChannelType.LOOPBACK)
public class P16LoopbackKeyNotify extends ChannelMessagePacket {

    public byte[] key;

    @Override
    public void writeContents(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeByteArray(key, LengthPrefix.NONE);
    }

    @Override
    public void readContents(PacketBuffer buffer, KeyProvider keyProvider) {
        key = buffer.readBytes(64);
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x16;
    }

    @Override
    public void persistContents(PacketDirection packetDirection) {
        Storage.getDatabase().loopbackKeyNotifyDao().insert(LoopbackKeyNotify.fromPacket(this));
    }
}
