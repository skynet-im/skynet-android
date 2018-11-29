package de.vectordata.skynet.net;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.KeyProvider;
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
import de.vectordata.skynet.net.packet.Packet;

public class PacketHandler {

    private static final Packet[] REGISTERED_PACKETS = new Packet[]{
            null,
            new P01ConnectionResponse()
    };

    private KeyProvider keyProvider;

    public PacketHandler(KeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }

    void handlePacket(byte id, byte[] payload) {
        int uId = id & 0xFF;
        if (uId >= REGISTERED_PACKETS.length)
            return;

        Packet packet = REGISTERED_PACKETS[id];
        if (packet == null)
            return;

        PacketBuffer buffer = new PacketBuffer(payload);
        packet.readPacket(buffer, keyProvider);
        packet.handlePacket(this);
    }

    public void handlePacket(P01ConnectionResponse packet) {

    }

    public void handlePacket(P03CreateAccountResponse packet) {

    }

    public void handlePacket(P05DeleteAccountResponse packet) {

    }

    public void handlePacket(P07CreateSessionResponse packet) {

    }

    public void handlePacket(P09RestoreSessionResponse packet) {

    }

    public void handlePacket(P0ACreateChannel packet) {

    }

    public void handlePacket(P0BChannelMessage packet) {

    }

    public void handlePacket(P0CChannelMessageResponse packet) {

    }

    public void handlePacket(P0DMessageBlock packet) {

    }

    public void handlePacket(P0FSyncFinished packet) {

    }

    public void handlePacket(P10RealTimeMessage packet) {

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

    }

    public void handlePacket(P18PublicKeys packet) {

    }

    public void handlePacket(P19DerivedKey packet) {

    }

    public void handlePacket(P1AVerifiedKeys packet) {

    }
}
