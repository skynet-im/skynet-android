package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import de.vectordata.skynet.net.packet.P0BChannelMessage;

@Entity(tableName = "dependencies", foreignKeys = @ForeignKey(entity = ChannelMessage.class,
        parentColumns = {"channelId", "messageId"},
        childColumns = {"srcChannelId", "srcMessageId"},
        onDelete = ForeignKey.CASCADE),
        primaryKeys = {"srcChannelId", "srcMessageId"}
)
public class Dependency {

    private long srcChannelId;

    private long srcMessageId;

    private long dstAccountId;

    private long dstChannelId;

    private long dstMessageId;

    public static Dependency fromPacket(P0BChannelMessage packet, P0BChannelMessage.Dependency packetChild) {
        Dependency dependency = new Dependency();
        dependency.srcChannelId = packet.channelId;
        dependency.srcMessageId = packet.messageId;
        dependency.dstAccountId = packetChild.accountId;
        dependency.dstChannelId = packetChild.channelId;
        dependency.dstMessageId = packetChild.messageId;
        return dependency;
    }

    public long getSrcChannelId() {
        return srcChannelId;
    }

    public void setSrcChannelId(long srcChannelId) {
        this.srcChannelId = srcChannelId;
    }

    public long getSrcMessageId() {
        return srcMessageId;
    }

    public void setSrcMessageId(long srcMessageId) {
        this.srcMessageId = srcMessageId;
    }

    public long getDstAccountId() {
        return dstAccountId;
    }

    public void setDstAccountId(long dstAccountId) {
        this.dstAccountId = dstAccountId;
    }

    public long getDstChannelId() {
        return dstChannelId;
    }

    public void setDstChannelId(long dstChannelId) {
        this.dstChannelId = dstChannelId;
    }

    public long getDstMessageId() {
        return dstMessageId;
    }

    public void setDstMessageId(long dstMessageId) {
        this.dstMessageId = dstMessageId;
    }
}
