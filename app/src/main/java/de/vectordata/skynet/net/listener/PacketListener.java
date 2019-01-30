package de.vectordata.skynet.net.listener;

import de.vectordata.skynet.net.packet.base.Packet;

public interface PacketListener {

    void onPacket(Packet packet);

}
