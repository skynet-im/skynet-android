package de.vectordata.skynet.net.response;

import de.vectordata.skynet.net.packet.base.Packet;

/**
 * Created by Twometer on 11.12.2018.
 * (c) 2018 Twometer
 */
class AwaiterItem {

    private Class<? extends Packet> packetClass;
    private ResponseHandler handler;

    AwaiterItem(Class<? extends Packet> packetClass, ResponseHandler handler) {
        this.packetClass = packetClass;
        this.handler = handler;
    }

    Class<? extends Packet> getPacketClass() {
        return packetClass;
    }

    ResponseHandler getHandler() {
        return handler;
    }

}
