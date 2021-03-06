package de.vectordata.skynet.net.packet;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.model.ConnectionState;
import de.vectordata.skynet.net.packet.annotation.AllowState;
import de.vectordata.skynet.net.packet.base.AbstractPacket;

@AllowState(ConnectionState.UNAUTHENTICATED)
public class P02CreateAccount extends AbstractPacket {

    public String accountName;
    public byte[] keyHash;

    public P02CreateAccount() {
    }

    public P02CreateAccount(String accountName, byte[] keyHash) {
        this.accountName = accountName;
        this.keyHash = keyHash;
    }

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
        buffer.writeString(accountName, LengthPrefix.SHORT);
        buffer.writeByteArray(keyHash, LengthPrefix.NONE);
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
    }

    @Override
    public void handlePacket(PacketHandler handler) {
    }

    @Override
    public byte getId() {
        return 0x02;
    }
}
