package de.vectordata.skynet.net.response;

import de.vectordata.skynet.net.packet.base.Packet;

/**
 * Created by Twometer on 11.12.2018.
 * (c) 2018 Twometer
 */
class AnyAwaiterItem<T extends Packet> extends BaseAwaiterItem<T> {

    private Class<T> packetClass;

    AnyAwaiterItem(Class<T> packetClass, ResponseHandler<T> handler) {
        super(handler);
        this.packetClass = packetClass;
    }

    @Override
    public boolean matches(Packet packet) {
        return packet.getClass() == packetClass;
    }

}
