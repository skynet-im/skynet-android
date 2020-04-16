package de.vectordata.skynet.net.client;

import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class SslClient {

    private static final String TAG = "SslClient";

    private SSLSocketFactory sslSocketFactory;

    private ManagedSocket socket;

    private SslClientListener listener;

    public SslClient(InputStream certStream) {
        sslSocketFactory = createFactory(certStream);
    }

    public void connect(String host, int port) {
        (new Thread(() -> {
            try {
                socket = new ManagedSocket(sslSocketFactory.createSocket());
                socket.connect(host, port);
                listener.onConnectionOpened();
                receiveLoop();
            } catch (IOException e) {
                e.printStackTrace();
                listener.onConnectionClosed();
            }
        })).start();
    }

    public void sendPacket(int id, byte[] payload) {
        PacketBuffer buffer = new PacketBuffer(payload.length + 4);
        buffer.writeInt32((payload.length << 8) | (byte) id);
        buffer.writeByteArray(payload, LengthPrefix.NONE);

        socket.send(buffer.toArray());
    }

    public void disconnect() {
        socket.disconnect();
    }

    public void setListener(SslClientListener listener) {
        this.listener = listener;
    }

    private void receiveLoop() {
        while (socket.isConnected()) {
            try {
                byte id = socket.receiveByte();

                byte[] lengthData = socket.receive(3);
                int length = (lengthData[0] & 0xFF) | ((lengthData[1] << 8) & 0xFF00) | ((lengthData[2] << 16) & 0xFF0000);

                byte[] payload = socket.receive(length);
                listener.onPacketReceived(id, payload);
            } catch (IOException e) {
                Log.e(TAG, "Receive loop interrupted", e);
                break;
            }
        }
        listener.onConnectionClosed();
    }

    private SSLSocketFactory createFactory(InputStream certStream) {
        try {
            Certificate certificate = CertificateFactory.getInstance("X.509").generateCertificate(certStream);

            KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
            store.load(null, null);
            store.setCertificateEntry("ca", certificate);

            TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            factory.init(store);

            String tlsVersion = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? "TLSv1.3" : "TLSv1.2";
            SSLContext context = SSLContext.getInstance(tlsVersion);
            context.init(null, factory.getTrustManagers(), new SecureRandom());
            return context.getSocketFactory();
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException | IOException e) {
            Log.e(TAG, "Failed to create SSLSocket factory", e);
        }
        return null;
    }

}
