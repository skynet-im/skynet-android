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
import de.vectordata.skynet.jobengine.JobEngine;
import de.vectordata.skynet.net.messages.MessageInterface;
import de.vectordata.skynet.net.packet.P0BChannelMessage;
import de.vectordata.skynet.ui.notification.INotificationManager;
import de.vectordata.skynet.ui.notification.NotificationManagerFactory;

public class SkynetContext implements KeyProvider {

    private static SkynetContext currentContext;

    private JobEngine jobEngine;
    private MessageInterface messageInterface;
    private NetworkManager networkManager;
    private INotificationManager notificationManager;

    private SkynetContext() {
        jobEngine = new JobEngine();
        notificationManager = (new NotificationManagerFactory()).createManager();
        messageInterface = new MessageInterface(this);
        networkManager = new NetworkManager(this);
        networkManager.connect();
    }

    public void recreateNetworkManager() {
        this.networkManager = new NetworkManager(this);
        this.networkManager.connect();
    }

    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public MessageInterface getMessageInterface() {
        return messageInterface;
    }

    public INotificationManager getNotificationManager() {
        return notificationManager;
    }

    public JobEngine getJobEngine() {
        return jobEngine;
    }

    @Override
    public KeyStore getMessageKeys(P0BChannelMessage message) {
        Channel channel = Storage.getDatabase().channelDao().getById(message.channelId);
        if (channel.getChannelType() == ChannelType.LOOPBACK)
            return Storage.getSession().getSessionKeys().getLoopbackChannelKeys();
        if (channel.getChannelType() != ChannelType.DIRECT)
            throw new IllegalStateException("Cannot request encryption keys for " + channel.getChannelType());

        Channel accountDataChannel = Storage.getDatabase().channelDao().getByType(channel.getCounterpartId(), ChannelType.ACCOUNT_DATA);
        Channel loopbackChannel = Storage.getDatabase().channelDao().getByType(Storage.getSession().getAccountId(), ChannelType.LOOPBACK);

        ChannelKey privateKey = Storage.getDatabase().channelKeyDao().getLast(loopbackChannel.getChannelId(), KeyType.PRIVATE);
        ChannelKey publicKey = Storage.getDatabase().channelKeyDao().getLast(accountDataChannel.getChannelId(), KeyType.PUBLIC);

        byte[] ecKey = EC.deriveKey(privateKey.getDerivationKey(), publicKey.getDerivationKey());
        byte[] sha512 = HashProvider.sha512(ecKey);

        return KeyStore.from64ByteArray(sha512);
    }

    public boolean isInSync() {
        return getNetworkManager().isInSync();
    }

    public static SkynetContext getCurrent() {
        if (currentContext == null)
            currentContext = new SkynetContext();
        return currentContext;
    }

}
