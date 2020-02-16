package de.vectordata.skynet.net.messages;

import java.util.Random;

import de.vectordata.skynet.jobengine.jobs.ChannelMessageJob;
import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.response.ResponseAwaiter;

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
     * @param config    The message config
     * @param packet    The content packet
     * @return An awaitable for the response
     */
    public ResponseAwaiter send(long channelId, ChannelMessageConfig config, ChannelMessagePacket packet) {
        PreparedMessage message = prepare(channelId, newId(), config, packet);
        message.persist(PersistenceMode.DATABASE);
        return skynetContext.getNetworkManager().sendPacket(message.getChannelMessage());
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
        PreparedMessage message = prepare(channelId, messageId, config, packet);
        message.persist(persistenceMode);
        skynetContext.getJobEngine().schedule(new ChannelMessageJob(message.getChannelMessage()));
    }

    private PreparedMessage prepare(long channelId, long messageId, ChannelMessageConfig config, ChannelMessagePacket packet) {
        /*P0BCnelMessage container = new P0BCelMessage();
        container.channelId = channelId;
        container.messageId = messageId;
        container.senderId = Storage.getSession().getAccountId();
        container.packetVersion = PACKET_VERSION;
        container.messageFlags = config.getMessageFlags();
        container.fileId = config.getFileId();
        container.contentPacketId = packet.getId();
        container.contentPacketVersion = PACKET_VERSION;
        container.fileKey = config.getFileKey();
        container.dependencies = config.getDependencies();
        container.dispatchTime = DateTime.now();

        PacketBuffer buffer = new PacketBuffer();
        packet.writePacket(buffer, skynetContext);
        container.contentPacket = buffer.toArray();

        return new PreparedMessage(container, packet);*/

    }

}
