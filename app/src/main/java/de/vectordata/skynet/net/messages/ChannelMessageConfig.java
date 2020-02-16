package de.vectordata.skynet.net.messages;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;
import de.vectordata.skynet.net.packet.model.MessageFlags;

public class ChannelMessageConfig {

    public static final long ANY_ACCOUNT = 0;

    private byte messageFlags;

    private long fileId;

    private byte[] fileKey;

    private List<ChannelMessagePacket.NetDependency> dependencies = new ArrayList<>();

    public static ChannelMessageConfig create() {
        return new ChannelMessageConfig();
    }

    public ChannelMessageConfig addFlag(byte messageFlags) {
        this.messageFlags |= messageFlags;
        return this;
    }

    public ChannelMessageConfig setAttachedFile(long fileId, byte[] fileKey) {
        this.fileId = fileId;
        this.fileKey = fileKey;
        addFlag(MessageFlags.FILE_ATTACHED);
        return this;
    }

    public ChannelMessageConfig addDependency(long accountId, long messageId) {
        return addDependency(new ChannelMessagePacket.NetDependency(accountId, messageId));
    }

    private ChannelMessageConfig addDependency(ChannelMessagePacket.NetDependency dependency) {
        dependencies.add(dependency);
        return this;
    }

    byte getMessageFlags() {
        return messageFlags;
    }

    long getFileId() {
        return fileId;
    }

    byte[] getFileKey() {
        return fileKey;
    }

    List<ChannelMessagePacket.NetDependency> getDependencies() {
        return dependencies;
    }

}
