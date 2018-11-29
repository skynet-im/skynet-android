package de.vectordata.skynet.net.packet;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.KeyProvider;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.packet.annotation.ChannelMessage;

@ChannelMessage(ChannelType.LOOPBACK)
public class P17PrivateKeys implements Packet {

    public List<byte[]> keys = new ArrayList<>();

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeByte((byte) keys.size());
        for(byte[] key : keys)
            buffer.writeByteArray(key, true);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        int count = buffer.readByte();
        for(int i = 0; i < count; i++)
            keys.add(buffer.readByteArray());
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x17;
    }
}
