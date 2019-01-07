package de.vectordata.skynet.net;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.crypto.keys.KeyStore;
import de.vectordata.skynet.net.messages.MessageInterface;

public class SkynetContext implements KeyProvider {

    private static SkynetContext currentContext;

    private MessageInterface messageInterface;
    private NetworkManager networkManager;

    private SkynetContext() {
        messageInterface = new MessageInterface(this);
        networkManager = new NetworkManager(this);
        networkManager.connect();
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public MessageInterface getMessageInterface() {
        return messageInterface;
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
