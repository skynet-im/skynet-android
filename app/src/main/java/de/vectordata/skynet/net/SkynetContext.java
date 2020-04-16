package de.vectordata.skynet.net;

import android.content.Context;

import java.io.InputStream;

import de.vectordata.skynet.SkynetApplication;
import de.vectordata.skynet.crypto.EC;
import de.vectordata.skynet.crypto.hash.HashProvider;
import de.vectordata.skynet.crypto.keys.ChannelKeys;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelKey;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.data.model.enums.KeyType;
import de.vectordata.skynet.jobengine.JobEngine;
import de.vectordata.skynet.net.messages.MessageInterface;
import de.vectordata.skynet.net.state.AppState;
import de.vectordata.skynet.ui.notification.INotificationManager;
import de.vectordata.skynet.ui.notification.NotificationManagerFactory;

public class SkynetContext implements KeyProvider {

    private static SkynetContext currentContext;

    private JobEngine jobEngine;
    private MessageInterface messageInterface;
    private NetworkManager networkManager;
    private INotificationManager notificationManager;
    private AppState appState;

    private SkynetContext() {
        jobEngine = new JobEngine();
        notificationManager = (new NotificationManagerFactory()).createManager();
        messageInterface = new MessageInterface(this);
        networkManager = new NetworkManager(this);
        appState = new AppState();
    }

    public static SkynetContext getCurrent() {
        if (currentContext == null)
            currentContext = new SkynetContext();
        return currentContext;
    }

    public void initialize(Context context) {
        InputStream certStream = context.getResources().openRawResource(SkynetApplication.CERTIFICATE_RES);
        networkManager.initialize(certStream);
        networkManager.connect();
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

    public AppState getAppState() {
        return appState;
    }

    @Override
    public ChannelKeys getChannelKeys(long channelId) {
        Channel channel = Storage.getDatabase().channelDao().getById(channelId);
        if (channel == null)
            throw new IllegalArgumentException("Cannot request keys for null channel");
        if (channel.getChannelType() == ChannelType.LOOPBACK)
            return Storage.getSession().getSessionKeys().getLoopbackChannelKeys();
        if (channel.getChannelType() != ChannelType.DIRECT)
            throw new IllegalStateException("Cannot request encryption keys for " + channel.getChannelType());

        ChannelKey privateKey = Storage.getDatabase().channelKeyDao().getFromChannel(Storage.getSession().getAccountId(), ChannelType.LOOPBACK, KeyType.PRIVATE);
        ChannelKey publicKey = Storage.getDatabase().channelKeyDao().getFromChannel(channel.getCounterpartId(), ChannelType.ACCOUNT_DATA, KeyType.PUBLIC);

        if (privateKey == null)
            throw new IllegalStateException("No private key in the loopback channel");
        if (publicKey == null)
            throw new IllegalStateException("No public key found in target account data channel");

        byte[] ecKey = EC.deriveKey(privateKey.getDerivationKey(), publicKey.getDerivationKey());
        byte[] sha512 = HashProvider.sha512(ecKey);

        return ChannelKeys.from64ByteArray(sha512);
    }

    public boolean isInSync() {
        return getNetworkManager().isInSync();
    }

}
