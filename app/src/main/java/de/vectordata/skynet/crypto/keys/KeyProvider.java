package de.vectordata.skynet.crypto.keys;

public interface KeyProvider {

    ChannelKeys getChannelKeys(long channelId);

}
