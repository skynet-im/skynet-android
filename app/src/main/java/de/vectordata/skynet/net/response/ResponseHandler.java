package de.vectordata.skynet.net.response;

import de.vectordata.skynet.net.packet.base.Packet;

/**
 * Created by Twometer on 11.12.2018.
 * (c) 2018 Twometer
 */
public interface ResponseHandler<T extends Packet> {

    void handle(T packet);

}
