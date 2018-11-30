package de.vectordata.skynet.net.packet.base;

import de.vectordata.skynet.net.packet.P10RealTimeMessage;

public abstract class RealtimeMessagePacket implements Packet {

    private P10RealTimeMessage parent;

    public P10RealTimeMessage getParent() {
        return parent;
    }

    public void setParent(P10RealTimeMessage parent) {
        this.parent = parent;
    }
}
