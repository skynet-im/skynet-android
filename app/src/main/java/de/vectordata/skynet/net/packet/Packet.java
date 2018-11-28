package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.net.PacketHandler;

public interface Packet {

    void writePacket(PacketBuffer buffer);

    void readPacket(PacketBuffer buffer);

    void handlePacket(PacketHandler handler);

    byte getId();

}
