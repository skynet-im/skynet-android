package de.vectordata.skynet.event;

import de.vectordata.skynet.net.packet.model.HandshakeState;

public class HandshakeFailedEvent {

    private HandshakeState state;

    private String newVersion;

    public HandshakeFailedEvent(HandshakeState state, String newVersion) {
        this.state = state;
        this.newVersion = newVersion;
    }

    public HandshakeState getState() {
        return state;
    }

    public String getNewVersion() {
        return newVersion;
    }
}
