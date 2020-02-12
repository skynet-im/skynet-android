package de.vectordata.skynet.net.packet;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.skynet.crypto.keys.KeyProvider;
import de.vectordata.skynet.net.PacketHandler;
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
            String accountName = buffer.readString();
            int packetCount = buffer.readUInt16();
            List<ForwardedPacket> forwardedPackets = new ArrayList<>();
            for (int j = 0; j < packetCount; j++) {
                forwardedPackets.add(new ForwardedPacket(buffer.readByte(), buffer.readByteArray()));
            }
            results.add(new Result(accountId, accountName, forwardedPackets));
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

    public class Result {
        public long accountId;
        public String accountName;
        public List<ForwardedPacket> forwardedPackets;

        public Result(long accountId, String accountName, List<ForwardedPacket> forwardedPackets) {
            this.accountId = accountId;
            this.accountName = accountName;
            this.forwardedPackets = forwardedPackets;
        }
    }

    public class ForwardedPacket {
        public byte packetId;
        public byte[] packetContent;

        public ForwardedPacket(byte packetId, byte[] packetContent) {
            this.packetId = packetId;
            this.packetContent = packetContent;
        }
    }
}

