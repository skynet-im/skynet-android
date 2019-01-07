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

    public void addFlag(byte messageFlags) {
        messageFlags |= messageFlags;
    }

    public void setAttachedFile(long fileId, byte[] fileKey) {
        this.fileId = fileId;
        this.fileKey = fileKey;
        addFlag(MessageFlags.FILE_ATTACHED);
    }

    public void addDependency(P0BChannelMessage.Dependency dependency) {
        dependencies.add(dependency);
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
