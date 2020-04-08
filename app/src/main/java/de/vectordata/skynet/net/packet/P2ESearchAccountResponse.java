package de.vectordata.skynet.net.packet;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
import de.vectordata.skynet.net.client.LengthPrefix;
import de.vectordata.skynet.net.client.PacketBuffer;
import de.vectordata.skynet.net.packet.base.AbstractPacket;

public class P2ESearchAccountResponse extends AbstractPacket {

    public List<Result> results = new ArrayList<>();

    @Override
    public void writePacket(PacketBuffer buffer, KeyProvider keyProvider) {
    }

    @Override
    public void readPacket(PacketBuffer buffer, KeyProvider keyProvider) {
        results.clear();
        int count = buffer.readUInt16();
        for (int i = 0; i < count; i++) {
            long accountId = buffer.readInt64();
            String accountName = buffer.readString(LengthPrefix.SHORT);
            results.add(new Result(accountId, accountName));
        }
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        handler.handlePacket(this);
    }

    @Override
    public byte getId() {
        return 0x2E;
    }

    public static class Result {
        public long accountId;
        public String accountName;

        public Result(long accountId, String accountName) {
            this.accountId = accountId;
            this.accountName = accountName;
        }
    }
}

