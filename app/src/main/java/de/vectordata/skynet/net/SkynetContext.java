package de.vectordata.skynet.net;

import de.vectordata.skynet.crypto.KeyProvider;
import de.vectordata.skynet.crypto.KeyStore;

public class SkynetContext implements KeyProvider {

    private NetworkManager networkManager;

    public SkynetContext() {
        networkManager = new NetworkManager(this);
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    @Override
    public KeyStore getChannelKeys(long channelId) {
        return null; // TODO
    }
}