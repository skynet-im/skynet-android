package de.vectordata.skynet.net.packet.base;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;

public interface Packet {

    void writePacket(PacketBuffer buffer, KeyProvider keyProvider);

    void readPacket(PacketBuffer buffer, KeyProvider keyProvider);

    void handlePacket(PacketHandler handler);

    byte getId();

}
