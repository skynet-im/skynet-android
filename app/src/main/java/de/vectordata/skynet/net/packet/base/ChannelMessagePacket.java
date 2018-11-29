package de.vectordata.skynet.net.packet.base;

import de.vectordata.skynet.net.packet.P0BChannelMessage;

public abstract class ChannelMessagePacket implements Packet {

    private P0BChannelMessage parent;

    public P0BChannelMessage getParent() {
        return parent;
    }

    public void setParent(P0BChannelMessage parent) {
        this.parent = parent;
    }
}
