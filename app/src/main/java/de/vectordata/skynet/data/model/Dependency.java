package de.vectordata.skynet.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.List;

import de.vectordata.skynet.net.packet.P0BChannelMessage;

@Entity(tableName = "dependencies", foreignKeys = @ForeignKey(
        entity = ChannelMessage.class,
        parentColumns = {"channelId", "messageId"},
        childColumns = {"srcChannelId", "srcMessageId"},
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE),
        indices = {@Index(value = {"srcChannelId", "srcMessageId"})}
)
public class Dependency {

    @PrimaryKey(autoGenerate = true)
    private long internalId;

    private long srcChannelId;

    private long srcMessageId;

    private long dstAccountId;

    private long dstChannelId;

    private long dstMessageId;

    public static Dependency[] arrayFromPacket(P0BChannelMessage packet, List<P0BChannelMessage.Dependency> children) {
        Dependency[] dependencies = new Dependency[children.size()];
        for (int i = 0; i < children.size(); i++)
            dependencies[i] = fromPacket(packet, children.get(i));
        return dependencies;
    }

    private static Dependency fromPacket(P0BChannelMessage packet, P0BChannelMessage.Dependency packetChild) {
        Dependency dependency = new Dependency();
        dependency.srcChannelId = packet.channelId;
        dependency.srcMessageId = packet.messageId;
        dependency.dstAccountId = packetChild.accountId;
        dependency.dstChannelId = packetChild.channelId;
        dependency.dstMessageId = packetChild.messageId;
        return dependency;
    }

    public long getInternalId() {
        return internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
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
