package de.vectordata.skynet.net.messages;

import java.util.Random;

import de.vectordata.libjvsl.util.PacketBuffer;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.P0BChannelMessage;
import de.vectordata.skynet.net.packet.P10RealTimeMessage;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.base.RealtimeMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageFlags;
import de.vectordata.skynet.net.response.ResponseAwaiter;

public class MessageInterface {

    private static final Random idRandom = new Random();

    private static final int PACKET_VERSION = 1;

    private SkynetContext skynetContext;

    public MessageInterface(SkynetContext skynetContext) {
        this.skynetContext = skynetContext;
    }

    public ResponseAwaiter sendChannelMessage(Channel channel, ChannelMessageConfig config, ChannelMessagePacket packet) {
        return sendChannelMessage(channel.getChannelId(), config, packet);
    }

    public ResponseAwaiter sendChannelMessage(long channelId, ChannelMessageConfig config, ChannelMessagePacket packet) {
        PacketBuffer buffer = new PacketBuffer();
        packet.writePacket(buffer, skynetContext);

        P0BChannelMessage container = new P0BChannelMessage();
        container.channelId = channelId;
        container.messageId = newId();
        container.packetVersion = PACKET_VERSION;
        container.messageFlags = config.getMessageFlags();
        container.fileId = config.getFileId();
        container.contentPacketId = packet.getId();
        container.contentPacketVersion = PACKET_VERSION;
        container.contentPacket = buffer.toArray();
        container.fileKey = config.getFileKey();
        container.dependencies = config.getDependencies();

        container.writeToDatabase(PacketDirection.SEND);
        packet.setParent(container);
        packet.writeToDatabase(PacketDirection.SEND);

        return skynetContext.getNetworkManager().sendPacket(container);
    }

    public ResponseAwaiter sendRealTimeMessage(long channelId, RealtimeMessagePacket packet) {
        return sendRealTimeMessage(channelId, MessageFlags.NONE, packet);
    }

    public ResponseAwaiter sendRealTimeMessage(long channelId, byte flags, RealtimeMessagePacket packet) {
        P10RealTimeMessage container = new P10RealTimeMessage();
        container.channelId = channelId;
        container.messageFlags = flags;
        container.contentPacketId = packet.getId();
        PacketBuffer buffer = new PacketBuffer();
        packet.writePacket(buffer, skynetContext);
        container.contentPacket = buffer.toArray();
        return skynetContext.getNetworkManager().sendPacket(container);
    }

    public static long newId() {
        long id;
        do id = idRandom.nextLong(); while (id == 0);
        if (id > 0) id = -id;
        return id;
    }

}
