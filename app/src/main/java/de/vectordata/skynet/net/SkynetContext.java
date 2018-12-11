package de.vectordata.skynet.net;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.crypto.keys.KeyStore;

public class SkynetContext implements KeyProvider {

    private static SkynetContext currentContext;

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

    public static SkynetContext getCurrent() {
        if (currentContext == null)
            currentContext = new SkynetContext();
        return currentContext;
    }
}
