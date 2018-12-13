package de.vectordata.skynet.net;

/**
 * Created by Twometer on 12.12.2018.
 * (c) 2018 Twometer
 */
public enum ConnectionState {
    DISCONNECTED,
    CONNECTING,
    HANDSHAKING,
    AUTHENTICATING,
    UNAUTHENTICATED,
    AUTHENTICATED
}
