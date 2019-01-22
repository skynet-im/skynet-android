package de.vectordata.skynet.net;

import de.vectordata.skynet.crypto.EC;
import de.vectordata.skynet.crypto.hash.HashProvider;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.crypto.keys.KeyStore;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelKey;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.data.model.enums.KeyType;
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
        Channel channel = Storage.getDatabase().channelDao().getById(channelId);
        if (channel.getChannelType() == ChannelType.LOOPBACK)
            return Storage.getSession().getSessionKeys().getLoopbackChannelKeys();
        ChannelKey publicKey = Storage.getDatabase().channelKeyDao().getLast(channelId, KeyType.PUBLIC);
        ChannelKey privateKey = Storage.getDatabase().channelKeyDao().getLast(channelId, KeyType.PRIVATE);
        byte[] ecKey = EC.deriveKey(privateKey.getDerivationKey(), publicKey.getDerivationKey());
        byte[] sha512 = HashProvider.sha512(ecKey);
        return KeyStore.from64ByteArray(sha512);
    }

    public static SkynetContext getCurrent() {
        if (currentContext == null)
            currentContext = new SkynetContext();
        return currentContext;
    }

}
