package de.vectordata.skynet.net.client;

import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import de.vectordata.skynet.util.android.Handlers;

class ManagedSocket {

    private Socket socket;

    private InputStream inputStream;
    private OutputStream outputStream;

    private Handler sendHandler = Handlers.createOnThread(Handlers.THREAD_NETWORK);

    private volatile boolean connected;

    ManagedSocket(Socket socket) {
        this.socket = socket;
    }

    void connect(String host, int port) throws IOException {
        if (host == null || host.isEmpty())
            throw new IllegalArgumentException("The host must not be null");
        if (port < 0 || port > 65535)
            throw new IllegalArgumentException("The port must be in range from 0..65535");

        socket.setKeepAlive(true);
        socket.connect(new InetSocketAddress(host, port), 10000);

        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

        connected = true;
    }

    byte receiveByte() throws IOException {
        int data = inputStream.read();
        if (data == -1) {
            connected = false;
            throw new IOException("Connection lost: End of stream");
        }
        return (byte) data;
    }

    byte[] receive(int len) throws IOException {
        byte[] data = new byte[len];

        int total = 0;
        while (total < len) {
            try {
                int read = inputStream.read(data, total, len - total);
                if (read == -1) throw new IOException("Connection lost: End of stream");
                total += read;
            } catch (IOException e) {
                connected = false;
                throw e;
            }
        }

        return data;
    }

    void send(byte[] data) {
        sendHandler.post(() -> {
            try {
                outputStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
                connected = false;
            }
        });
    }

    void disconnect() {
        try {
            if (inputStream != null)
                inputStream.close();

            if (outputStream != null)
                outputStream.close();

            socket.close();
            connected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean isConnected() {
        return connected;
    }
}
