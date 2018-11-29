package de.vectordata.skynet.crypto;

public interface KeyProvider {

    KeyStore getChannelKeys(long channelId);

}
