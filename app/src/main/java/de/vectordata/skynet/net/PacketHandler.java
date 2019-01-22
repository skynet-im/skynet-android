package de.vectordata.skynet.net;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.EC;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelMessage;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.data.model.DaystreamMessage;
import de.vectordata.skynet.data.model.enums.ChannelType;
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
import de.vectordata.skynet.net.packet.P10RealTimeMessage;
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
import de.vectordata.skynet.net.packet.P26PersonalMessage;
import de.vectordata.skynet.net.packet.P27ProfileImage;
import de.vectordata.skynet.net.packet.P29DeviceList;
import de.vectordata.skynet.net.packet.P2ABackgroundImage;
import de.vectordata.skynet.net.packet.P2BOnlineState;
import de.vectordata.skynet.net.packet.P2CDeviceListDetails;
import de.vectordata.skynet.net.packet.P2ESearchAccountResponse;
import de.vectordata.skynet.net.packet.P2FCreateChannelResponse;
import de.vectordata.skynet.net.packet.P31FileUploadResponse;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.base.Packet;
import de.vectordata.skynet.net.packet.base.RealtimeMessagePacket;
import de.vectordata.skynet.net.packet.model.AsymmetricKey;
import de.vectordata.skynet.net.packet.model.CreateSessionError;
import de.vectordata.skynet.net.packet.model.KeyFormat;
import de.vectordata.skynet.net.packet.model.MessageFlags;
import de.vectordata.skynet.net.packet.model.OverrideAction;
import de.vectordata.skynet.net.packet.model.RestoreSessionError;
import de.vectordata.skynet.net.response.ResponseAwaiter;

public class PacketHandler {

    private KeyProvider keyProvider;
    private NetworkManager networkManager;
    private ResponseAwaiter responseAwaiter;

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

        packet.readPacket(new PacketBuffer(payload), keyProvider);

        if (packet instanceof ChannelMessagePacket) {
            ((ChannelMessagePacket) packet).setParent((P0BChannelMessage) parent);
            ((ChannelMessagePacket) packet).writeToDatabase(PacketDirection.RECEIVE);
        } else if (packet instanceof RealtimeMessagePacket)
            ((RealtimeMessagePacket) packet).setParent((P10RealTimeMessage) parent);

        packet.handlePacket(this);
        responseAwaiter.onPacket(packet);
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
        Channel channel = Storage.getDatabase().channelDao().getById(packet.tempChannelId);
        channel.setChannelId(packet.channelId);
        Storage.getDatabase().channelDao().update(channel);
    }

    public void handlePacket(P0BChannelMessage packet) {
        packet.writeToDatabase(PacketDirection.RECEIVE);
        handlePacket(packet.contentPacketId, packet.contentPacket, packet);
    }

    public void handlePacket(P0CChannelMessageResponse packet) {
        ChannelMessage message = Storage.getDatabase().channelMessageDao().getById(packet.channelId, packet.tempMessageId);
        message.setMessageId(packet.messageId);
        Storage.getDatabase().channelMessageDao().update(message);
    }

    public void handlePacket(P0FSyncFinished packet) {
        boolean hasKeys = Storage.getDatabase().channelKeyDao().hasKeys(ChannelType.LOOPBACK) != 0;
        if (!hasKeys) {
            EC.KeyMaterial signature = EC.generateKeypair();
            EC.KeyMaterial derivation = EC.generateKeypair();
            if (signature == null || derivation == null)
                return;
            Channel loopbackChannel = Storage.getDatabase().channelDao().getByType(Storage.getSession().getAccountId(), ChannelType.LOOPBACK);
            SkynetContext.getCurrent().getMessageInterface().sendChannelMessage(loopbackChannel,
                    new ChannelMessageConfig(),
                    new P17PrivateKeys(
                            new AsymmetricKey(KeyFormat.JAVA, signature.getPrivateKey()),
                            new AsymmetricKey(KeyFormat.JAVA, derivation.getPrivateKey())
                    )
            );
            SkynetContext.getCurrent().getMessageInterface().sendChannelMessage(loopbackChannel,
                    new ChannelMessageConfig().addFlag(MessageFlags.UNENCRYPTED),
                    new P18PublicKeys(
                            new AsymmetricKey(KeyFormat.JAVA, signature.getPublicKey()),
                            new AsymmetricKey(KeyFormat.JAVA, derivation.getPublicKey())
                    )
            );
        }
    }

    public void handlePacket(P10RealTimeMessage packet) {
        handlePacket(packet.contentPacketId, packet.contentPacket, packet);
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

    }

    public void handlePacket(P1CDirectChannelCustomization packet) {

    }

    public void handlePacket(P1DGroupChannelKeyNotify packet) {

    }

    public void handlePacket(P1EGroupChannelUpdate packet) {

    }

    public void handlePacket(P20ChatMessage packet) {

    }

    public void handlePacket(P21MessageOverride packet) {
        Channel channel = Storage.getDatabase().channelDao().getById(packet.getParent().channelId);
        if (channel.getChannelType() == ChannelType.PROFILE_DATA) {
            DaystreamMessage message = Storage.getDatabase().daystreamMessageDao().get(channel.getChannelId(), packet.messageId);
            if (packet.action == OverrideAction.DELETE)
                Storage.getDatabase().daystreamMessageDao().delete(message);
            else {
                message.setText(packet.newText);
                message.setEdited(true);
                Storage.getDatabase().daystreamMessageDao().update(message);
            }
        } else {
            ChatMessage message = Storage.getDatabase().chatMessageDao().query(packet.getParent().channelId, packet.messageId);
            if (packet.action == OverrideAction.DELETE) message.setText("\0");
            else {
                message.setText(packet.newText);
                message.setEdited(true);
            }
            Storage.getDatabase().chatMessageDao().update(message);
        }
    }

    public void handlePacket(P22MessageReceived packet) {

    }

    public void handlePacket(P23MessageRead packet) {

    }

    public void handlePacket(P24DaystreamMessage packet) {

    }

    public void handlePacket(P25Nickname packet) {

    }

    public void handlePacket(P26PersonalMessage packet) {

    }

    public void handlePacket(P27ProfileImage packet) {

    }

    public void handlePacket(P29DeviceList packet) {

    }

    public void handlePacket(P2ABackgroundImage packet) {

    }

    ////////////////////// Real time messages //////////////////////
    public void handlePacket(P2BOnlineState packet) {

    }

    public void handlePacket(P2CDeviceListDetails packet) {

    }

    ////////////////////// On demand packets //////////////////////
    public void handlePacket(P2ESearchAccountResponse packet) {

    }

    public void handlePacket(P31FileUploadResponse packet) {

    }
}
