package de.vectordata.skynet.net.packet;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.packet.base.Packet;

public class P06CreateSession implements Packet {

    public String accountName;
    public byte[] keyHash;
    public String fcmRegistrationToken;

    public P06CreateSession() {
    }

    public P06CreateSession(String accountName, byte[] keyHash, String fcmRegistrationToken) {
        this.accountName = accountName;
        this.keyHash = keyHash;
        this.fcmRegistrationToken = fcmRegistrationToken;
    }

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeString(accountName);
        buffer.writeByteArray(keyHash, false);
        buffer.writeString(fcmRegistrationToken);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
    }

    @Override
    public void handlePacket(PacketHandler handler) {
    }

    @Override
    public byte getId() {
        return 0x06;
    }
}
