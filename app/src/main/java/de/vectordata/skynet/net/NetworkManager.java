package de.vectordata.skynet.net;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.libjvsl.VSLClient;
import de.vectordata.libjvsl.VSLClientListener;
import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.auth.Authenticator;
import de.vectordata.skynet.auth.Session;
import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.net.listener.AuthenticationListener;
import de.vectordata.skynet.net.listener.ErrorListener;
import de.vectordata.skynet.net.listener.HandshakeListener;
import de.vectordata.skynet.net.model.ConnectionState;
import de.vectordata.skynet.net.packet.P00ConnectionHandshake;
import de.vectordata.skynet.net.packet.P01ConnectionResponse;
import de.vectordata.skynet.net.packet.annotation.AllowState;
import de.vectordata.skynet.net.packet.base.Packet;
import de.vectordata.skynet.net.packet.model.HandshakeState;
import de.vectordata.skynet.net.packet.model.RestoreSessionError;
import de.vectordata.skynet.net.response.ResponseAwaiter;
import de.vectordata.skynet.util.Constants;
import de.vectordata.skynet.util.Version;

public class NetworkManager implements VSLClientListener {

    private static final String TAG = "NetworkManager";

    private SkynetContext skynetContext;

    private VSLClient vslClient;
    private PacketHandler packetHandler;
    private ResponseAwaiter responseAwaiter = new ResponseAwaiter();
    private ConnectionState connectionState = ConnectionState.DISCONNECTED;

    private AuthenticationListener authenticationListener;
    private HandshakeListener handshakeListener;
    private ErrorListener errorListener;

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
        new Thread(() -> {
            vslClient = new VSLClient(Constants.PRODUCT_LATEST, Constants.PRODUCT_OLDEST);
            vslClient.setListener(this);
            boolean successfullyConnected = vslClient.connect(Constants.SERVER_IP, Constants.SERVER_PORT, Constants.SERVER_KEY);
            if (!successfullyConnected)
                onConnectionFailed();
        }).start();
    }

    public ResponseAwaiter sendPacket(Packet packet) {
        if (shouldCache(packet)) packetCache.add(packet);
        else {
            PacketBuffer buffer = new PacketBuffer();
            packet.writePacket(buffer, skynetContext);
            vslClient.sendPacket(packet.getId(), buffer.toArray());
        }
        return responseAwaiter;
    }

    private boolean shouldCache(Packet packet) {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            AllowState allowedState = packet.getClass().getAnnotation(AllowState.class);
            return allowedState == null || allowedState.value() != connectionState;
        }
        return false;
    }

    @Override
    public void onConnectionEstablished() {
        connectionState = ConnectionState.HANDSHAKING;
        Log.v(TAG, "Sending handshake...");
        sendPacket(new P00ConnectionHandshake(Version.PROTOCOL_VERSION, Version.APPLICATION_IDENTIFIER, Version.VERSION_CODE))
                .waitForPacket(P01ConnectionResponse.class, p -> {
                    if (p.handshakeState == HandshakeState.MUST_UPGRADE) {
                        Log.e(TAG, "Server rejected connection: version too old");
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
    public void onPacketReceived(byte id, byte[] payload) {
        packetHandler.handlePacket(id, payload);
    }

    @Override
    public void onConnectionClosed(String s) {
        Log.e(TAG, "Connection to server closed due to " + s);
        onConnectionFailed();
    }

    private void onConnectionFailed() {
        connectionState = ConnectionState.DISCONNECTED;
        Log.d(TAG, "Scheduling reconnect in 1 min");
        handler.postDelayed(this::connect, 60000);
        if (errorListener != null)
            errorListener.onConnectionFailed();
    }

    private void authenticate() {
        Session session = Storage.getSession();
        if (session == null || !session.isAuthenticated()) {
            connectionState = ConnectionState.UNAUTHENTICATED;
            return;
        }
        Authenticator.authenticate(session, err -> {
            if (err != RestoreSessionError.SUCCESS && authenticationListener != null)
                authenticationListener.onAuthFailed(err);
        });
    }

    private void raiseHandshakeEvent(HandshakeState state, String newVersion) {
        if (handshakeListener != null)
            handshakeListener.onInvalidState(state, newVersion);
    }

    public void setHandshakeListener(HandshakeListener handshakeListener) {
        this.handshakeListener = handshakeListener;
    }

    public void setAuthenticationListener(AuthenticationListener authenticationListener) {
        this.authenticationListener = authenticationListener;
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
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
        for (Packet packet : packetCache)
            sendPacket(packet);
        packetCache.clear();
    }

}
