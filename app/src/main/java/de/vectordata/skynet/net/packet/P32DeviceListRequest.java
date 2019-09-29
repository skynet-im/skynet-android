package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.packet.base.AbstractPacket;

public class P32DeviceListRequest extends AbstractPacket {
    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {

    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {

    }

    @Override
    public void handlePacket(PacketHandler handler) {

    }

    @Override
    public byte getId() {
        return 0x32;
    }
}
