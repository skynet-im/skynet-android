package de.vectordata.skynet.net.response;

import de.vectordata.skynet.net.packet.base.Packet;

public interface IAwaiterItem {

    boolean matches(Packet packet);

    void handle(Packet packet);

}
