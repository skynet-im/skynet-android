package de.vectordata.skynet.net.messages;

import java.util.ArrayList;
import java.util.List;

import de.vectordata.skynet.data.model.FileInfo;
import de.vectordata.skynet.net.packet.base.ChannelMessagePacket;

public class ChannelMessageConfig {

    public static final long ANY_ACCOUNT = 0;

    private byte messageFlags;

    private long fileId;

    private FileInfo attachedFile;

    private List<ChannelMessagePacket.NetDependency> dependencies = new ArrayList<>();

    public static ChannelMessageConfig create() {
        return new ChannelMessageConfig();
    }

    public ChannelMessageConfig addFlag(byte messageFlags) {
        this.messageFlags |= messageFlags;
        return this;
    }

    public FileInfo getAttachedFile() {
        return attachedFile;
    }

    public void setAttachedFile(FileInfo attachedFile) {
        this.attachedFile = attachedFile;
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

    List<ChannelMessagePacket.NetDependency> getDependencies() {
        return dependencies;
    }

}
