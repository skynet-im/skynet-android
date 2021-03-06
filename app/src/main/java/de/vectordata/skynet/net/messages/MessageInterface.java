package de.vectordata.skynet.net.messages;

import java.util.Random;

import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.jobengine.jobs.ChannelMessageJob;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.model.PacketDirection;
import de.vectordata.skynet.net.packet.annotation.Flags;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.response.PacketTask;
import de.vectordata.skynet.util.date.DateTime;

public class MessageInterface {

    private static final Random random = new Random();

    private static final int PACKET_VERSION = 1;

    private SkynetContext skynetContext;

    public MessageInterface(SkynetContext skynetContext) {
        this.skynetContext = skynetContext;
    }

    /**
     * Generates a random ID for a request message that is guaranteed to be negative and non-zero
     *
     * @return The random id
     */
    public static long newId() {
        long id;
        do id = random.nextLong(); while (id == 0);
        return id > 0 ? -id : id;
    }

    /**
     * Send a channel message packet, generating a new random message ID, without
     * using the job engine
     *
     * @param channelId The channel to send the message to
     * @param packet    The content packet
     * @return An awaitable for the response
     */
    public PacketTask send(long channelId, ChannelMessagePacket packet) {
        return send(channelId, new ChannelMessageConfig(), packet);
    }

    /**
     * Send a channel message packet, generating a new random message ID, without
     * using the job engine
     *
     * @param channelId The channel to send the message to
     * @param config    The message config
     * @param packet    The content packet
     * @return An awaitable for the response
     */
    public PacketTask send(long channelId, ChannelMessageConfig config, ChannelMessagePacket packet) {
        configure(packet, channelId, newId(), config);
        packet.persist(PacketDirection.SEND);
        return skynetContext.getNetworkManager().sendPacket(packet);
    }

    /**
     * Schedule sending a channel message packet, generating a new random message ID
     *
     * @param channelId The channel to send the message to
     * @param config    The message config
     * @param packet    The content packet
     */
    public void schedule(long channelId, ChannelMessageConfig config, ChannelMessagePacket packet) {
        schedule(channelId, newId(), config, packet, PersistenceMode.DATABASE);
    }

    /**
     * Schedule sending a channel message packet, using a custom message ID.
     * This is useful to for example retry a previously failed message attempt
     *
     * @param channelId       The channel to send the message to
     * @param messageId       The id number of the message
     * @param config          The message config
     * @param packet          The content packet
     * @param persistenceMode Whether to save to the database
     */
    public void schedule(long channelId, long messageId, ChannelMessageConfig config, ChannelMessagePacket packet, PersistenceMode persistenceMode) {
        configure(packet, channelId, messageId, config);
        if (persistenceMode == PersistenceMode.DATABASE)
            packet.persist(PacketDirection.SEND);
        skynetContext.getJobEngine().schedule(new ChannelMessageJob(packet));
    }

    private void configure(ChannelMessagePacket packet, long channelId, long messageId, ChannelMessageConfig config) {
        packet.packetVersion = PACKET_VERSION;
        packet.channelId = channelId;
        packet.messageId = messageId;
        packet.senderId = Storage.getSession().getAccountId();
        packet.dispatchTime = DateTime.now();
        packet.messageFlags = config.getMessageFlags();
        packet.dependencies = config.getDependencies();
        packet.attachedFile = config.getAttachedFile();
        packet.fileId = config.getFileId();

        Flags flags = packet.getClass().getAnnotation(Flags.class);
        if (flags != null)
            packet.messageFlags |= flags.value();
    }

}
