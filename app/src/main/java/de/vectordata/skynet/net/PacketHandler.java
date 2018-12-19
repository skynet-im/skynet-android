package de.vectordata.skynet.net;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.data.StorageAccess;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelKeypair;
import de.vectordata.skynet.data.model.ChannelMessage;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.data.model.DaystreamMessage;
import de.vectordata.skynet.data.model.Dependency;
import de.vectordata.skynet.data.model.enums.ChannelType;
import de.vectordata.skynet.net.model.AsymmetricKey;
import de.vectordata.skynet.net.model.CreateSessionError;
import de.vectordata.skynet.net.model.KeyRole;
import de.vectordata.skynet.net.model.OverrideAction;
import de.vectordata.skynet.net.model.RestoreSessionError;
import de.vectordata.skynet.net.packet.P01ConnectionResponse;
import de.vectordata.skynet.net.packet.P03CreateAccountResponse;
import de.vectordata.skynet.net.packet.P05DeleteAccountResponse;
import de.vectordata.skynet.net.packet.P07CreateSessionResponse;
import de.vectordata.skynet.net.packet.P09RestoreSessionResponse;
import de.vectordata.skynet.net.packet.P0ACreateChannel;
import de.vectordata.skynet.net.packet.P0BChannelMessage;
import de.vectordata.skynet.net.packet.P0CChannelMessageResponse;
import de.vectordata.skynet.net.packet.P0DMessageBlock;
import de.vectordata.skynet.net.packet.P0FSyncFinished;
import de.vectordata.skynet.net.packet.P10RealTimeMessage;
import de.vectordata.skynet.net.packet.P13QueueMailAddressChange;
import de.vectordata.skynet.net.packet.P14MailAddress;
import de.vectordata.skynet.net.packet.P15PasswordUpdate;
import de.vectordata.skynet.net.packet.P16LoopbackKeyNotify;
import de.vectordata.skynet.net.packet.P17PrivateKeys;
import de.vectordata.skynet.net.packet.P18PublicKeys;
import de.vectordata.skynet.net.packet.P19DerivedKey;
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
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.base.Packet;
import de.vectordata.skynet.net.packet.base.RealtimeMessagePacket;
import de.vectordata.skynet.net.response.ResponseAwaiter;

public class PacketHandler {

    private static final Packet[] REGISTERED_PACKETS = new Packet[]{
            null,
            new P01ConnectionResponse()
    };

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
        int uId = id & 0xFF;
        if (uId >= REGISTERED_PACKETS.length)
            return;

        Packet packet = REGISTERED_PACKETS[id];
        if (packet == null)
            return;

        if (packet instanceof ChannelMessagePacket)
            ((ChannelMessagePacket) packet).setParent((P0BChannelMessage) parent);

        if (packet instanceof RealtimeMessagePacket)
            ((RealtimeMessagePacket) packet).setParent((P10RealTimeMessage) parent);

        PacketBuffer buffer = new PacketBuffer(payload);
        packet.readPacket(buffer, keyProvider);
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
        StorageAccess.getDatabase().channelDao().insert(Channel.fromPacket(packet));
    }

    public void handlePacket(P0BChannelMessage packet) {
        Channel channel = StorageAccess.getDatabase().channelDao().getById(packet.channelId);
        if (packet.messageId > channel.getLatestMessage()) {
            channel.setLatestMessage(packet.messageId);
            StorageAccess.getDatabase().channelDao().update(channel);
        }

        StorageAccess.getDatabase().channelMessageDao().insert(ChannelMessage.fromPacket(packet));

        Dependency[] dependencies = new Dependency[packet.dependencies.size()];
        for (int i = 0; i < dependencies.length; i++)
            dependencies[i] = Dependency.fromPacket(packet, packet.dependencies.get(i));
        StorageAccess.getDatabase().dependencyDao().insert(dependencies);

        handlePacket(packet.contentPacketId, packet.contentPacket, packet);
    }

    public void handlePacket(P0CChannelMessageResponse packet) {

    }

    public void handlePacket(P0DMessageBlock packet) {

    }

    public void handlePacket(P0FSyncFinished packet) {

    }

    public void handlePacket(P10RealTimeMessage packet) {
        handlePacket(packet.contentPacketId, packet.contentPacket, packet);
    }

    public void handlePacket(P13QueueMailAddressChange packet) {

    }

    public void handlePacket(P14MailAddress packet) {

    }

    public void handlePacket(P15PasswordUpdate packet) {

    }

    public void handlePacket(P16LoopbackKeyNotify packet) {

    }

    public void handlePacket(P17PrivateKeys packet) {
        StorageAccess.getDatabase().channelKeypairDao().upsert(createPrivateKey(packet.getParent().channelId, KeyRole.SIGNATURE, packet.signatureKey));
        StorageAccess.getDatabase().channelKeypairDao().upsert(createPrivateKey(packet.getParent().channelId, KeyRole.DERIVATION, packet.derivationKey));
    }

    public void handlePacket(P18PublicKeys packet) {
        StorageAccess.getDatabase().channelKeypairDao().upsert(createPublicKey(packet.getParent().channelId, KeyRole.SIGNATURE, packet.signatureKey));
        StorageAccess.getDatabase().channelKeypairDao().upsert(createPublicKey(packet.getParent().channelId, KeyRole.DERIVATION, packet.derivationKey));
    }

    private ChannelKeypair createPublicKey(long channelId, KeyRole keyRole, AsymmetricKey key) {
        ChannelKeypair keypair = new ChannelKeypair();
        keypair.setChannelId(channelId);
        keypair.setKeyRole(keyRole);
        keypair.setPublicKeyFormat(key.format);
        keypair.setPublicKey(key.key);
        return keypair;
    }

    private ChannelKeypair createPrivateKey(long channelId, KeyRole keyRole, AsymmetricKey key) {
        ChannelKeypair keypair = new ChannelKeypair();
        keypair.setChannelId(channelId);
        keypair.setKeyRole(keyRole);
        keypair.setPrivateKeyFormat(key.format);
        keypair.setPrivateKey(key.key);
        return keypair;
    }

    public void handlePacket(P19DerivedKey packet) {

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
        StorageAccess.getDatabase().chatMessageDao().insert(ChatMessage.fromPacket(packet));
    }

    public void handlePacket(P21MessageOverride packet) {
        Channel channel = StorageAccess.getDatabase().channelDao().getById(packet.getParent().channelId);
        if (channel.getChannelType() == ChannelType.PROFILE_DATA) {
            DaystreamMessage message = StorageAccess.getDatabase().daystreamMessageDao().get(channel.getChannelId(), packet.messageId);
            if (packet.action == OverrideAction.DELETE)
                StorageAccess.getDatabase().daystreamMessageDao().delete(message);
            else {
                message.setText(packet.newText);
                message.setEdited(true);
                StorageAccess.getDatabase().daystreamMessageDao().update(message);
            }
        } else {
            ChatMessage message = StorageAccess.getDatabase().chatMessageDao().query(packet.getParent().channelId, packet.messageId);
            if (packet.action == OverrideAction.DELETE) message.setText("\0");
            else {
                message.setText(packet.newText);
                message.setEdited(true);
            }
            StorageAccess.getDatabase().chatMessageDao().update(message);
        }

    }

    public void handlePacket(P22MessageReceived packet) {

    }

    public void handlePacket(P23MessageRead packet) {

    }

    public void handlePacket(P24DaystreamMessage packet) {
        DaystreamMessage message = DaystreamMessage.fromPacket(packet);
        StorageAccess.getDatabase().daystreamMessageDao().insert(message);
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

    public void handlePacket(P2BOnlineState packet) {

    }

    public void handlePacket(P2CDeviceListDetails packet) {

    }

    public void handlePacket(P2ESearchAccountResponse packet) {

    }
}
