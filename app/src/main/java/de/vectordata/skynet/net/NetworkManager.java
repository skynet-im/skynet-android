package de.vectordata.skynet.net;

import android.util.Log;

import de.vectordata.libjvsl.VSLClient;
import de.vectordata.libjvsl.VSLClientListener;
import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.net.packet.base.Packet;
import de.vectordata.skynet.util.Constants;

public class NetworkManager implements VSLClientListener {

    private static final String TAG = "NetworkManager";

    private SkynetContext skynetContext;

    private VSLClient vslClient;
    private PacketHandler packetHandler;

    private boolean connected;

    public NetworkManager(SkynetContext skynetContext) {
        this.skynetContext = skynetContext;
    }

    public void connect() {
        Log.i(TAG, "Connecting to server...");
        packetHandler = new PacketHandler(skynetContext);
        vslClient = new VSLClient(Constants.PRODUCT_LATEST, Constants.PRODUCT_OLDEST);
        vslClient.setListener(this);
        vslClient.connect(Constants.SERVER_IP, Constants.SERVER_PORT, Constants.SERVER_KEY);
    }

    public boolean isConnected() {
        return connected;
    }

    public void sendPacket(Packet packet) {
        PacketBuffer buffer = new PacketBuffer();
        packet.writePacket(buffer, skynetContext);
        vslClient.sendPacket(packet.getId(), buffer.toArray());
    }

    @Override
    public void onConnectionEstablished() {
        Log.i(TAG, "Connection to server established");
        connected = true;
    }

    @Override
    public void onPacketReceived(byte id, byte[] payload) {
        packetHandler.handlePacket(id, payload);
    }

    @Override
    public void onConnectionClosed(String s) {
        Log.i(TAG, "Connection to server closed");
        connected = false;
    }
}
