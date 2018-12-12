package de.vectordata.skynet.net;

import android.os.Handler;
import android.util.Log;

import de.vectordata.libjvsl.VSLClient;
import de.vectordata.libjvsl.VSLClientListener;
import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.net.listener.HandshakeListener;
import de.vectordata.skynet.net.model.HandshakeState;
import de.vectordata.skynet.net.packet.P00ConnectionHandshake;
import de.vectordata.skynet.net.packet.P01ConnectionResponse;
import de.vectordata.skynet.net.packet.base.Packet;
import de.vectordata.skynet.net.response.ResponseAwaiter;
import de.vectordata.skynet.util.Constants;
import de.vectordata.skynet.util.Version;

public class NetworkManager implements VSLClientListener {

    private static final String TAG = "NetworkManager";

    private SkynetContext skynetContext;

    private VSLClient vslClient;
    private PacketHandler packetHandler;
    private ResponseAwaiter responseAwaiter = new ResponseAwaiter();
    private HandshakeListener handshakeListener;

    private ConnectionState connectionState;

    private Handler handler = new Handler();

    NetworkManager(SkynetContext skynetContext) {
        this.skynetContext = skynetContext;
    }

    void connect() {
        if (isConnected()) {
            Log.v(TAG, "connect() called but already connected to server");
            return;
        }

        Log.i(TAG, "Connecting to server...");
        responseAwaiter.initialize();
        packetHandler = new PacketHandler(responseAwaiter, skynetContext);
        vslClient = new VSLClient(Constants.PRODUCT_LATEST, Constants.PRODUCT_OLDEST);
        vslClient.setListener(this);
        vslClient.connect(Constants.SERVER_IP, Constants.SERVER_PORT, Constants.SERVER_KEY);
    }

    private boolean isConnected() {
        return connectionState == ConnectionState.CONNECTED;
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public ResponseAwaiter sendPacket(Packet packet) {
        PacketBuffer buffer = new PacketBuffer();
        packet.writePacket(buffer, skynetContext);
        vslClient.sendPacket(packet.getId(), buffer.toArray());
        return responseAwaiter;
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

                    connectionState = ConnectionState.CONNECTED;
                });
    }

    @Override
    public void onPacketReceived(byte id, byte[] payload) {
        packetHandler.handlePacket(id, payload);
    }

    @Override
    public void onConnectionClosed(String s) {
        Log.i(TAG, "Connection to server closed");
        connectionState = ConnectionState.DISCONNECTED;
        handler.postDelayed(this::connect, 60000);
    }

    public void setHandshakeListener(HandshakeListener handshakeListener) {
        this.handshakeListener = handshakeListener;
    }

    private void raiseHandshakeEvent(HandshakeState state, String newVersion) {
        if (handshakeListener != null)
            handshakeListener.onInvalidState(state, newVersion);
    }
}
