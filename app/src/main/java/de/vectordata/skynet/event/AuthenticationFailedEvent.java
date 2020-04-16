package de.vectordata.skynet.event;

import de.vectordata.skynet.net.packet.model.RestoreSessionStatus;

public class AuthenticationFailedEvent {

    private RestoreSessionStatus error;

    public AuthenticationFailedEvent(RestoreSessionStatus error) {
        this.error = error;
    }

    public RestoreSessionStatus getError() {
        return error;
    }

}
