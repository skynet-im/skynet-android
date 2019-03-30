package de.vectordata.skynet.event;

import de.vectordata.skynet.net.packet.model.RestoreSessionError;

public class AuthenticationFailedEvent {

    private RestoreSessionError error;

    public AuthenticationFailedEvent(RestoreSessionError error) {
        this.error = error;
    }

    public RestoreSessionError getError() {
        return error;
    }

}
