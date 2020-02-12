package de.vectordata.skynet.net;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import de.vectordata.skynet.crypto.EC;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelMessage;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.data.model.enums.MessageState;
import de.vectordata.skynet.event.ChatMessageSentEvent;
import de.vectordata.skynet.event.PacketEvent;
import de.vectordata.skynet.event.SyncFinishedEvent;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.messages.ChannelMessageConfig;
import de.vectordata.skynet.net.model.ConnectionState;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.P01ConnectionResponse;
import de.vectordata.skynet.net.packet.P03CreateAccountResponse;
import de.vectordata.skynet.net.packet.P05DeleteAccountResponse;
import de.vectordata.skynet.net.packet.P07CreateSessionResponse;
import de.vectordata.skynet.net.packet.P09RestoreSessionResponse;
import de.vectordata.skynet.net.packet.P0ACreateChannel;
import de.vectordata.skynet.net.packet.P0BChannelMessage;
import de.vectordata.skynet.net.packet.P0CChannelMessageResponse;
import de.vectordata.skynet.net.packet.P0FSyncFinished;
import de.vectordata.skynet.net.packet.P13QueueMailAddressChange;
import de.vectordata.skynet.net.packet.P14MailAddress;
import de.vectordata.skynet.net.packet.P15PasswordUpdate;
import de.vectordata.skynet.net.packet.P16LoopbackKeyNotify;
import de.vectordata.skynet.net.packet.P17PrivateKeys;
import de.vectordata.skynet.net.packet.P18PublicKeys;
import de.vectordata.skynet.net.packet.P19KeypairReference;
import de.vectordata.skynet.net.packet.P1AVerifiedKeys;
import de.vectordata.skynet.net.packet.P1BDirectChannelUpdate;
import de.vectordata.skynet.net.packet.P1CDirectChannelCustomization;
import de.vectordata.skynet.net.packet.P1DGroupChannelKeyNotify;
import de.vectordata.skynet.net.packet.P1EGroupChannelUpdate;
import de.vectordata.skynet.net.packet.P20ChatMessage;
import de.vectordata.skynet.net.packet.P21MessageOverride;
import de.vectordata.skynet.net.packet.P22MessageReceived;
import de.vectordata.skynet.net.packet.P23MessageRead;
import de.vectordata.skynet.net.packet.P24DaystreamMessage;
import de.vectordata.skynet.net.packet.P25Nickname;
import de.vectordata.skynet.net.packet.P26Bio;
import de.vectordata.skynet.net.packet.P27ProfileImage;
import de.vectordata.skynet.net.packet.P28BlockList;
import de.vectordata.skynet.net.packet.P29DeviceList;
import de.vectordata.skynet.net.packet.P2ABackgroundImage;
import de.vectordata.skynet.net.packet.P2BOnlineState;
import de.vectordata.skynet.net.packet.P2CChannelAction;
import de.vectordata.skynet.net.packet.P2ESearchAccountResponse;
import de.vectordata.skynet.net.packet.P2FCreateChannelResponse;
import de.vectordata.skynet.net.packet.P31FileUploadResponse;
import de.vectordata.skynet.net.packet.P33DeviceListResponse;
import de.vectordata.skynet.net.packet.P34SetClientState;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.base.Packet;
import de.vectordata.skynet.net.packet.model.AsymmetricKey;
import de.vectordata.skynet.net.packet.model.CreateChannelError;
import de.vectordata.skynet.net.packet.model.CreateSessionError;
import de.vectordata.skynet.net.packet.model.KeyFormat;
import de.vectordata.skynet.net.packet.model.MessageFlags;
import de.vectordata.skynet.net.packet.model.RestoreSessionError;
import de.vectordata.skynet.net.response.ResponseAwaiter;

public class PacketHandler {

    private static final String TAG = "PacketHandler";

    private KeyProvider keyProvider;
    private NetworkManager networkManager;
    private ResponseAwaiter responseAwaiter;

    private boolean inSync;

    public PacketHandler(KeyProvider keyProvider, NetworkManager networkManager, ResponseAwaiter responseAwaiter) {
        this.keyProvider = keyProvider;
        this.networkManager = networkManager;
        this.responseAwaiter = responseAwaiter;
    }

    void handlePacket(byte id, byte[] payload) {
        handlePacket(id, payload, null);
    }

    private void handlePacket(byte id, byte[] payload, Packet parent) {
        if (!PacketRegistry.isValidId(id))
            return;

        Packet packet = PacketRegistry.getPacket(id);
        if (packet == null)
            return;

        if (packet instanceof ChannelMessagePacket)
            ((ChannelMessagePacket) packet).setParent((P0BChannelMessage) parent);

        packet.readPacket(new PacketBuffer(payload), keyProvider);

        if (!packet.validatePacket())
            return;

        if (packet instanceof ChannelMessagePacket)
            ((ChannelMessagePacket) packet).writeToDatabase(PacketDirection.RECEIVE);

        Log.d(TAG, "Handling packet 0x" + Integer.toHexString(packet.getId()));

        packet.handlePacket(this);
        responseAwaiter.onPacket(packet);
        EventBus.getDefault().post(new PacketEvent(packet));
    }

    public void handlePacket(P01ConnectionResponse packet) {

    }

    public void handlePacket(P03CreateAccountResponse packet) {

    }

    public void handlePacket(P05DeleteAccountResponse packet) {

    }

    public void handlePacket(P07CreateSessionResponse packet) {
        if (packet.errorCode == CreateSessionError.SUCCESS) {
            networkManager.setConnectionState(ConnectionState.AUTHENTICATED);
            networkManager.releaseCache();
        } else
            networkManager.setConnectionState(ConnectionState.UNAUTHENTICATED);
    }

    public void handlePacket(P09RestoreSessionResponse packet) {
        if (packet.errorCode == RestoreSessionError.SUCCESS) {
            networkManager.setConnectionState(ConnectionState.AUTHENTICATED);
            networkManager.releaseCache();
        } else
            networkManager.setConnectionState(ConnectionState.UNAUTHENTICATED);
    }

    public void handlePacket(P0ACreateChannel packet) {
        Storage.getDatabase().channelDao().insert(Channel.fromPacket(packet));
    }

    public void handlePacket(P2FCreateChannelResponse packet) {
        if (packet.errorCode == CreateChannelError.SUCCESS) {
            Channel channel = Storage.getDatabase().channelDao().getById(packet.tempChannelId);
            channel.setChannelId(packet.channelId);
            Storage.getDatabase().channelDao().update(channel);
        } else Storage.getDatabase().channelDao().deleteById(packet.tempChannelId);
    }

    public void handlePacket(P0BChannelMessage packet) {
        packet.writeToDatabase(PacketDirection.RECEIVE);
        handlePacket(packet.contentPacketId, packet.contentPacket, packet);
    }

    public void handlePacket(P0CChannelMessageResponse packet) {
        ChannelMessage message = Storage.getDatabase().channelMessageDao().getById(packet.channelId, packet.tempMessageId);
        message.setMessageId(packet.messageId);
        message.setDispatchTime(packet.dispatchTime);
        Storage.getDatabase().channelMessageDao().update(message);

        ChatMessage chatMessage = Storage.getDatabase().chatMessageDao().query(message.getChannelId(), message.getMessageId());
        if (chatMessage != null) {
            chatMessage.setMessageState(MessageState.SENT);
            Storage.getDatabase().chatMessageDao().update(chatMessage);
            EventBus.getDefault().post(new ChatMessageSentEvent());
        }

        Channel channel = Storage.getDatabase().channelDao().getById(packet.channelId);
        if (packet.messageId > channel.getLatestMessage()) {
            channel.setLatestMessage(packet.messageId);
            Storage.getDatabase().channelDao().update(channel);
        }
    }

    public void handlePacket(P0FSyncFinished packet) {
        boolean hasKeys = Storage.getDatabase().channelKeyDao().hasKeys(ChannelType.LOOPBACK) != 0;
        if (!hasKeys) {
            Log.d(TAG, "No loopback keys found, generating...");
            EC.KeyMaterial signature = EC.generateKeypair();
            EC.KeyMaterial derivation = EC.generateKeypair();
            if (signature == null || derivation == null)
                return;
            Channel loopbackChannel = Storage.getDatabase().channelDao().getByType(Storage.getSession().getAccountId(), ChannelType.LOOPBACK);
            SkynetContext.getCurrent().getMessageInterface().send(loopbackChannel.getChannelId(),
                    new ChannelMessageConfig(),
                    new P17PrivateKeys(
                            new AsymmetricKey(KeyFormat.JAVA, signature.getPrivateKey()),
                            new AsymmetricKey(KeyFormat.JAVA, derivation.getPrivateKey())
                    )
            ).waitForPacket(P0CChannelMessageResponse.class, response -> {
                Channel accountDataChannel = Storage.getDatabase().channelDao().getByType(Storage.getSession().getAccountId(), ChannelType.ACCOUNT_DATA);
                SkynetContext.getCurrent().getMessageInterface().send(accountDataChannel.getChannelId(),
                        new ChannelMessageConfig()
                                .addFlag(MessageFlags.UNENCRYPTED)
                                .addDependency(Storage.getSession().getAccountId(), loopbackChannel.getChannelId(), response.messageId),
                        new P18PublicKeys(
                                new AsymmetricKey(KeyFormat.JAVA, signature.getPublicKey()),
                                new AsymmetricKey(KeyFormat.JAVA, derivation.getPublicKey())
                        )
                );
            });
        }

        inSync = true;
        EventBus.getDefault().post(new SyncFinishedEvent());
        for (ChatMessage msg : Storage.getDatabase().chatMessageDao().queryUnconfirmed(Storage.getSession().getAccountId())) {
            sendReceiveConfirmation(msg.getChannelId(), msg.getMessageId());
        }
        for (ChatMessage msg : Storage.getDatabase().chatMessageDao().queryUnread()) {
            ChannelMessage channelMsg = Storage.getDatabase().channelMessageDao().getById(msg.getChannelId(), msg.getMessageId());
            if (channelMsg.getSenderId() == Storage.getSession().getAccountId())
                continue;
            SkynetContext.getCurrent().getNotificationManager().onMessageReceived(msg.getChannelId(), msg.getMessageId(), msg.getText());
        }
        SkynetContext.getCurrent().getNetworkManager().sendPacket(new P34SetClientState(SkynetContext.getCurrent().getAppState().getOnlineState()));
    }


    ////////////////////// Channel messages //////////////////////

    public void handlePacket(P13QueueMailAddressChange packet) {

    }

    public void handlePacket(P14MailAddress packet) {

    }

    public void handlePacket(P15PasswordUpdate packet) {

    }

    public void handlePacket(P16LoopbackKeyNotify packet) {

    }

    public void handlePacket(P17PrivateKeys packet) {

    }

    public void handlePacket(P18PublicKeys packet) {

    }

    public void handlePacket(P19KeypairReference packet) {

    }

    public void handlePacket(P1AVerifiedKeys packet) {

    }

    public void handlePacket(P1BDirectChannelUpdate packet) {
        /*long me = Storage.getSession().getAccountId();
        P0BChannelMessage parent = packet.getParent();
        P0BChannelMessage.Dependency keypairReferenceDependency = parent.findDependency(d -> d.accountId == me);
        List<Dependency> dependencies = Storage.getDatabase().dependencyDao().getDependencies(keypairReferenceDependency.channelId, keypairReferenceDependency.messageId);
        ChannelKey privateKey;
        ChannelKey publicKey;*/
    }

    public void handlePacket(P1CDirectChannelCustomization packet) {

    }

    public void handlePacket(P1DGroupChannelKeyNotify packet) {

    }

    public void handlePacket(P1EGroupChannelUpdate packet) {

    }

    public void handlePacket(P20ChatMessage packet) {
        if (!inSync) return; // Only send receive confirmations live if in sync
        if (packet.getParent().senderId == Storage.getSession().getAccountId())
            return; // Don't send receive confirmations for my own messages
        sendReceiveConfirmation(packet.getParent().channelId, packet.getParent().messageId);
        SkynetContext.getCurrent().getNotificationManager().onMessageReceived(packet.getParent().channelId, packet.getParent().messageId, packet.text);
    }

    public void handlePacket(P21MessageOverride packet) {
    }

    // TODO: Only update message state if EVERYONE in the channel received it. Also, save those who received/read it.
    public void handlePacket(P22MessageReceived packet) {
        P0BChannelMessage.Dependency dependency = packet.getParent().singleDependency();
        setMessageState(dependency.channelId, dependency.messageId, MessageState.DELIVERED);
    }

    public void handlePacket(P23MessageRead packet) {
        P0BChannelMessage.Dependency dependency = packet.getParent().singleDependency();
        setMessageState(dependency.channelId, dependency.messageId, MessageState.SEEN);
        SkynetContext.getCurrent().getNotificationManager().onMessageDeleted(dependency.channelId, dependency.messageId);
    }

    private void sendReceiveConfirmation(long channelId, long messageId) {
        SkynetContext.getCurrent().getMessageInterface()
                .send(channelId,
                        new ChannelMessageConfig().addDependency(ChannelMessageConfig.ANY_ACCOUNT, channelId, messageId),
                        new P22MessageReceived()
                );
        setMessageState(channelId, messageId, MessageState.DELIVERED);
    }

    private void setMessageState(long channelId, long messageId, MessageState messageState) {
        ChatMessage message = Storage.getDatabase().chatMessageDao().query(channelId, messageId);
        if (message.getMessageState() == MessageState.SEEN) // Do not un-see the message
            return;
        message.setMessageState(messageState);
        if (messageState == MessageState.SEEN)
            message.setUnread(false);
        Storage.getDatabase().chatMessageDao().update(message);
    }

    public void handlePacket(P24DaystreamMessage packet) {

    }

    public void handlePacket(P25Nickname packet) {

    }

    public void handlePacket(P26Bio packet) {

    }

    public void handlePacket(P27ProfileImage packet) {

    }

    public void handlePacket(P28BlockList packlet) {

    }

    public void handlePacket(P29DeviceList packet) {

    }

    public void handlePacket(P2ABackgroundImage packet) {

    }

    public void handlePacket(P2BOnlineState packet) {

    }

    public void handlePacket(P2CChannelAction packet) {
        SkynetContext.getCurrent().getAppState().setChannelAction(packet.channelId, packet.accountId, packet.channelAction);
    }

    ////////////////////// On demand packets //////////////////////
    public void handlePacket(P2ESearchAccountResponse packet) {

    }

    public void handlePacket(P31FileUploadResponse packet) {

    }

    public void handlePacket(P33DeviceListResponse packet) {

    }

    boolean isInSync() {
        return inSync;
    }

}
