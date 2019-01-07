package de.vectordata.skynet.net.listener;

import de.vectordata.skynet.net.packet.model.HandshakeState;

/**
 * Created by Twometer on 12.12.2018.
 * (c) 2018 Twometer
 */
public interface HandshakeListener {

    void onInvalidState(HandshakeState state, String newVersion);

}
