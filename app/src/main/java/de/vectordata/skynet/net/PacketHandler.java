package de.vectordata.skynet.net;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import de.vectordata.skynet.crypto.EC;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelKey;
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
import de.vectordata.skynet.net.packet.P0BSyncStarted;
import de.vectordata.skynet.net.packet.P0CChannelMessageResponse;
import de.vectordata.skynet.net.packet.P0DDeleteChannel;
import de.vectordata.skynet.net.packet.P0FSyncFinished;
import de.vectordata.skynet.net.packet.P13QueueMailAddressChange;
import de.vectordata.skynet.net.packet.P14MailAddress;
import de.vectordata.skynet.net.packet.P15PasswordUpdate;
import de.vectordata.skynet.net.packet.P17PrivateKeys;
import de.vectordata.skynet.net.packet.P18PublicKeys;
import de.vectordata.skynet.net.packet.P19ArchiveChannel;
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
import de.vectordata.skynet.net.packet.P33DeviceListResponse;
import de.vectordata.skynet.net.packet.P34SetClientState;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.base.Packet;
import de.vectordata.skynet.net.packet.model.CreateChannelStatus;
import de.vectordata.skynet.net.packet.model.CreateSessionStatus;
import de.vectordata.skynet.net.packet.model.RestoreSessionStatus;

public class PacketHandler {

    private static final String TAG = "PacketHandler";

    private KeyProvider keyProvider;
    private NetworkManager networkManager;

    private int numCorruptedMessages;
    private boolean inSync;

    public PacketHandler(KeyProvider keyProvider, NetworkManager networkManager) {
        this.keyProvider = keyProvider;
        this.networkManager = networkManager;
    }

    void handlePacket(byte id, byte[] payload) {
        if (!PacketRegistry.isValidId(id))
            return;

        Packet packet = PacketRegistry.getPacket(id);
        if (packet == null)
            return;

        if (payload == null) {
            Log.w(TAG, "Received corrupted packet 0x" + Integer.toHexString(id));
            return;
        }

        packet.readPacket(new PacketBuffer(payload), keyProvider);

        if (packet instanceof ChannelMessagePacket) {
            ChannelMessagePacket message = (ChannelMessagePacket) packet;
            Flags flags = message.getClass().getAnnotation(Flags.class);
            if (flags != null)
                if ((message.messageFlags | flags.value()) != message.messageFlags)
                    throw new IllegalStateException(String.format("Incoming channel message lacks required message flags (got: %s, required at least: %d)", message.messageFlags, flags.value()));

            if (message.isCorrupted) {
                numCorruptedMessages++;
                Log.e(TAG, "Dropped corrupted channel message");
            }
        }

        if (!packet.validatePacket())
            return;

        if (packet instanceof ChannelMessagePacket)
            ((ChannelMessagePacket) packet).persist(PacketDirection.RECEIVE);

        packet.handlePacket(this);
        EventBus.getDefault().post(new PacketEvent(packet));
    }

    public void handlePacket(P01ConnectionResponse packet) {

    }

    public void handlePacket(P03CreateAccountResponse packet) {

    }

    public void handlePacket(P05DeleteAccountResponse packet) {

    }

    public void handlePacket(P07CreateSessionResponse packet) {
        if (packet.statusCode == CreateSessionStatus.SUCCESS) {
            networkManager.setConnectionState(ConnectionState.AUTHENTICATED);
            networkManager.releaseCache();
        } else
            networkManager.setConnectionState(ConnectionState.UNAUTHENTICATED);
    }

    public void handlePacket(P09RestoreSessionResponse packet) {
        if (packet.statusCode == RestoreSessionStatus.SUCCESS) {
            networkManager.setConnectionState(ConnectionState.AUTHENTICATED);
            networkManager.releaseCache();
        } else
            networkManager.setConnectionState(ConnectionState.UNAUTHENTICATED);
    }

    public void handlePacket(P0ACreateChannel packet) {
        Storage.getDatabase().channelDao().insert(Channel.fromPacket(packet));
    }

    public void handlePacket(P2FCreateChannelResponse packet) {
        if (packet.statusCode == CreateChannelStatus.SUCCESS) {
            Channel channel = Storage.getDatabase().channelDao().getById(packet.tempChannelId);
            channel.setChannelId(packet.channelId);
            Storage.getDatabase().channelDao().update(channel);
        } else Storage.getDatabase().channelDao().deleteById(packet.tempChannelId);
    }

    public void handlePacket(P0DDeleteChannel packet) {

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

    public void handlePacket(P0BSyncStarted packet) {
        Log.d(TAG, "Resynchronizing (" + packet.minCount + ") ...");
        inSync = false;
        numCorruptedMessages = 0;
    }

    public void handlePacket(P0FSyncFinished packet) {
        Channel loopbackChannel = Storage.getDatabase().channelDao().getByType(Storage.getSession().getAccountId(), ChannelType.LOOPBACK);
        Channel accountDataChannel = Storage.getDatabase().channelDao().getByType(Storage.getSession().getAccountId(), ChannelType.ACCOUNT_DATA);

        // Check if there is a key in the account data channel
        boolean hasKeys = Storage.getDatabase().channelKeyDao().countKeys(accountDataChannel.getChannelId()) != 0;
        if (!hasKeys)
            regenerateLoopbackKeys(loopbackChannel, accountDataChannel);

        inSync = true;
        EventBus.getDefault().post(new SyncFinishedEvent());

        // Update client state with the server
        SkynetContext.getCurrent().getNetworkManager().sendPacket(new P34SetClientState(SkynetContext.getCurrent().getAppState().getOnlineState()));

        // Send receive confirmations for all messages that are yet to be confirmed
        for (ChatMessage msg : Storage.getDatabase().chatMessageDao().queryUnconfirmed(Storage.getSession().getAccountId())) {
            sendReceiveConfirmation(msg.getChannelId(), msg.getMessageId());
        }

        // Update notifications for unread messages
        for (ChatMessage msg : Storage.getDatabase().chatMessageDao().queryUnread()) {
            ChannelMessage channelMsg = Storage.getDatabase().channelMessageDao().getById(msg.getChannelId(), msg.getMessageId());
            if (channelMsg.getSenderId() == Storage.getSession().getAccountId())
                continue;
            SkynetContext.getCurrent().getNotificationManager().onMessageReceived(msg.getChannelId(), msg.getMessageId(), msg.getText());
        }

        Log.d(TAG, "Synchronized with the server");
    }


    ////////////////////// Channel messages //////////////////////

    public void handlePacket(P13QueueMailAddressChange packet) {

    }

    public void handlePacket(P14MailAddress packet) {

    }

    public void handlePacket(P15PasswordUpdate packet) {

    }

    public void handlePacket(P17PrivateKeys packet) {
        Storage.getDatabase().channelKeyDao().insert(ChannelKey.fromPacket(packet));
    }

    public void handlePacket(P18PublicKeys packet) {
        Storage.getDatabase().channelKeyDao().insert(ChannelKey.fromPacket(packet));
    }

    public void handlePacket(P19ArchiveChannel packet) {

    }

    public void handlePacket(P1AVerifiedKeys packet) {

    }

    public void handlePacket(P1BDirectChannelUpdate packet) {

    }

    public void handlePacket(P1CDirectChannelCustomization packet) {

    }

    public void handlePacket(P1DGroupChannelKeyNotify packet) {

    }

    public void handlePacket(P1EGroupChannelUpdate packet) {
    }

    public void handlePacket(P20ChatMessage packet) {
        if (!inSync) return; // Only send receive confirmations live if in sync
        if (packet.isSentByMe())
            return; // Don't send receive confirmations for my own messages

        sendReceiveConfirmation(packet.channelId, packet.messageId);
        SkynetContext.getCurrent().getNotificationManager().onMessageReceived(packet.channelId, packet.messageId, packet.text);
    }

    public void handlePacket(P21MessageOverride packet) {

    }

    // TODO: Only update message state if EVERYONE in the channel received it. Also, save those who received/read it.
    public void handlePacket(P22MessageReceived packet) {
        ChannelMessagePacket.NetDependency dependency = packet.singleDependency();
        setMessageState(packet.channelId, dependency.messageId, MessageState.DELIVERED);
    }

    public void handlePacket(P23MessageRead packet) {
        ChannelMessagePacket.NetDependency dependency = packet.singleDependency();
        setMessageState(packet.channelId, dependency.messageId, MessageState.SEEN);
        SkynetContext.getCurrent().getNotificationManager().onMessageDeleted(packet.channelId, dependency.messageId);
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

    public void handlePacket(P33DeviceListResponse packet) {

    }

    ////////////////////// Utility methods //////////////////////
    private void regenerateLoopbackKeys(Channel loopbackChannel, Channel accountDataChannel) {
        Log.d(TAG, "Incomplete or missing loopback channel keys. Regenerating...");
        Storage.getDatabase().channelKeyDao().dropKeys(loopbackChannel.getChannelId());

        EC.KeyMaterial signature = EC.generateKeypair();
        EC.KeyMaterial derivation = EC.generateKeypair();
        if (signature == null || derivation == null)
            return;

        P17PrivateKeys privateKeys = new P17PrivateKeys(signature.getPrivateKey(), derivation.getPrivateKey());
        P18PublicKeys publicKeys = new P18PublicKeys(signature.getPublicKey(), derivation.getPublicKey());

        // First transmit the private keys
        SkynetContext.getCurrent().getMessageInterface().send(loopbackChannel.getChannelId(), privateKeys)
                .waitForSuccess(r -> {
                    // If that was successful, store them in the database
                    Storage.getDatabase().channelKeyDao().insert(ChannelKey.fromPacket(privateKeys));

                    // Create a dependency to the private key that we just created
                    ChannelMessageConfig config = ChannelMessageConfig.create()
                            .addDependency(Storage.getSession().getAccountId(), r.messageId);

                    // And transmit the public key
                    SkynetContext.getCurrent().getMessageInterface().send(accountDataChannel.getChannelId(), config, publicKeys)
                            .waitForSuccess(r2 -> Storage.getDatabase().channelKeyDao().insert(ChannelKey.fromPacket(publicKeys)));
                });
    }

    private void sendReceiveConfirmation(long channelId, long messageId) {
        SkynetContext.getCurrent().getMessageInterface()
                .send(channelId,
                        new ChannelMessageConfig().addDependency(ChannelMessageConfig.ANY_ACCOUNT, messageId),
                        new P22MessageReceived()
                ).waitForSuccess(response -> setMessageState(channelId, messageId, MessageState.DELIVERED));
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

    boolean isInSync() {
        return inSync;
    }

    int getNumCorruptedMessages() {
        return numCorruptedMessages;
    }
}
