package de.vectordata.skynet.net.messages;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.skynet.net.packet.P0BChannelMessage;
import de.vectordata.skynet.net.packet.model.MessageFlags;

public class ChannelMessageConfig {

    private byte messageFlags;

    private long fileId;

    private byte[] fileKey;

    private List<P0BChannelMessage.Dependency> dependencies = new ArrayList<>();

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

    public ChannelMessageConfig addDependency(long accountId, long channelId, long messageId) {
        return addDependency(new P0BChannelMessage.Dependency(accountId, channelId, messageId));
    }

    public ChannelMessageConfig addDependency(P0BChannelMessage.Dependency dependency) {
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

    List<P0BChannelMessage.Dependency> getDependencies() {
        return dependencies;
    }

}
