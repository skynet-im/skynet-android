package de.vectordata.skynet.net.client;

public interface SslClientListener {

    void onConnectionOpened();

    void onConnectionClosed();

    void onPacketReceived(byte id, byte[] payload);

}
