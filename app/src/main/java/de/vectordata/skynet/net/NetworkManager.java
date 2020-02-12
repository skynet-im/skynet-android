package de.vectordata.skynet.net;

import android.os.Handler;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.skynet.SkynetApplication;
import de.vectordata.skynet.auth.Authenticator;
import de.vectordata.skynet.auth.Session;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.event.AuthenticationFailedEvent;
import de.vectordata.skynet.event.AuthenticationSuccessfulEvent;
import de.vectordata.skynet.event.ConnectionFailedEvent;
import de.vectordata.skynet.event.HandshakeFailedEvent;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.client.SslClient;
import de.vectordata.skynet.net.client.SslClientListener;
import de.vectordata.skynet.net.model.ConnectionState;
import de.vectordata.skynet.net.packet.P00ConnectionHandshake;
import de.vectordata.skynet.net.packet.P01ConnectionResponse;
import de.vectordata.skynet.net.packet.P0BChannelMessage;
import de.vectordata.skynet.net.packet.annotation.AllowState;
import de.vectordata.skynet.net.packet.base.Packet;
import de.vectordata.skynet.net.packet.model.HandshakeState;
import de.vectordata.skynet.net.packet.model.RestoreSessionError;
import de.vectordata.skynet.net.response.ResponseAwaiter;

public class NetworkManager implements SslClientListener {

    private static final String TAG = "NetworkManager";

    private SkynetContext skynetContext;

    private SslClient sslClient;
    private PacketHandler packetHandler;
    private ResponseAwaiter responseAwaiter = new ResponseAwaiter();
    private ConnectionState connectionState = ConnectionState.DISCONNECTED;

    private List<Packet> packetCache = new ArrayList<>();

    private Handler handler = new Handler();

    NetworkManager(SkynetContext skynetContext) {
        this.skynetContext = skynetContext;
    }

    public void connect() {
        if (connectionState != ConnectionState.DISCONNECTED) {
            Log.v(TAG, "connect() called but not disconnected from server");
            return;
        }

        Log.i(TAG, "Connecting to server...");
        connectionState = ConnectionState.CONNECTING;

        responseAwaiter.initialize();
        packetHandler = new PacketHandler(skynetContext, this, responseAwaiter);

        sslClient = new SslClient(/* TODO Certificate stream */);
        sslClient.connect(SkynetApplication.SERVER_IP, SkynetApplication.SERVER_PORT);
    }

    public void disconnect() {
        sslClient.disconnect();
    }

    public ResponseAwaiter sendPacket(Packet packet) {
        if (connectionState != ConnectionState.AUTHENTICATED && packet instanceof P0BChannelMessage)
            return responseAwaiter;

        if (shouldCache(packet)) packetCache.add(packet);
        else {
            PacketBuffer buffer = new PacketBuffer();
            packet.writePacket(buffer, skynetContext);
            Log.d(TAG, "Sending packet 0x" + Integer.toHexString(packet.getId()));
            sslClient.sendPacket(packet.getId(), buffer.toArray());
        }
        return responseAwaiter;
    }

    private boolean shouldCache(Packet packet) {
        // Never cache channel messages. They may be more complex than just sending a packet
        // and are therefore handled by the JobEngine
        if (packet instanceof P0BChannelMessage)
            return false;

        // If no connection is present, and the packet in question does not have an AllowState
        // annotation for the current state, we cache the packet.
        if (connectionState != ConnectionState.AUTHENTICATED) {
            AllowState allowedState = packet.getClass().getAnnotation(AllowState.class);
            return allowedState == null || allowedState.value() != connectionState;
        }

        // If there is a connection, there is no need to cache any packets
        return false;
    }

    @Override
    public void onConnectionOpened() {
        connectionState = ConnectionState.HANDSHAKING;
        Log.v(TAG, "Sending handshake...");
        sendPacket(new P00ConnectionHandshake(SkynetApplication.PROTOCOL_VERSION, SkynetApplication.APPLICATION_IDENTIFIER, SkynetApplication.VERSION_CODE))
                .waitForPacket(P01ConnectionResponse.class, p -> {
                    if (p.handshakeState == HandshakeState.MUST_UPGRADE) {
                        Log.e(TAG, "Server rejected connection: version too old, update to " + p.latestVersionCode);
                        raiseHandshakeEvent(HandshakeState.MUST_UPGRADE, p.latestVersion);
                        connectionState = ConnectionState.DISCONNECTED;
                        return;
                    }

                    if (p.handshakeState == HandshakeState.CAN_UPGRADE) {
                        Log.w(TAG, "Server recommends upgrading to a later version");
                        raiseHandshakeEvent(HandshakeState.CAN_UPGRADE, p.latestVersion);
                    } else
                        Log.i(TAG, "Successfully connected the server");

                    connectionState = ConnectionState.AUTHENTICATING;
                    authenticate();
                });
    }

    @Override
    public void onConnectionClosed() {
        onConnectionFailed();
    }

    @Override
    public void onPacketReceived(byte id, byte[] payload) {
        Log.d(TAG, "Received packet 0x" + Integer.toHexString(id));
        packetHandler.handlePacket(id, payload);
    }

    private void onConnectionFailed() {
        connectionState = ConnectionState.DISCONNECTED;
        Log.d(TAG, "Scheduling reconnect in 10 sec");
        handler.postDelayed(this::connect, 10000);
        EventBus.getDefault().post(new ConnectionFailedEvent());
        packetCache.clear();
    }

    private void authenticate() {
        Session session = Storage.getSession();
        if (session == null || !session.isAuthenticated()) {
            connectionState = ConnectionState.UNAUTHENTICATED;
            return;
        }
        Authenticator.authenticate(session, err -> {
            if (err != RestoreSessionError.SUCCESS)
                EventBus.getDefault().post(new AuthenticationFailedEvent(err));
            else
                EventBus.getDefault().post(new AuthenticationSuccessfulEvent());
        });
    }

    private void raiseHandshakeEvent(HandshakeState state, String newVersion) {
        EventBus.getDefault().post(new HandshakeFailedEvent(state, newVersion));
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    void setConnectionState(ConnectionState connectionState) {
        this.connectionState = connectionState;
    }

    void releaseCache() {
        if (this.connectionState != ConnectionState.AUTHENTICATED)
            return;
        Log.i(TAG, "Releasing packet cache with contents: " + packetCache.size());
        for (Packet packet : packetCache)
            sendPacket(packet);
        packetCache.clear();
    }

    boolean isInSync() {
        return packetHandler.isInSync();
    }

}
