package de.vectordata.skynet.event;

import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;

public class CorruptedMessageEvent {

    private ChannelMessagePacket packet;

    public CorruptedMessageEvent(ChannelMessagePacket packet) {
        this.packet = packet;
    }

    public ChannelMessagePacket getPacket() {
        return packet;
    }
}
